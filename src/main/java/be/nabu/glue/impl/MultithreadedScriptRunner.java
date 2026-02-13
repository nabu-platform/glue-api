/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.glue.impl;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;

import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.glue.api.OutputFormatterProvider;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptFilter;
import be.nabu.glue.api.ScriptRepository;
import be.nabu.glue.api.ScriptRuntimeRunnableWrapper;
import be.nabu.glue.api.runs.CallLocation;
import be.nabu.glue.api.runs.GlueAttachment;
import be.nabu.glue.api.runs.ScriptResult;
import be.nabu.glue.api.runs.ScriptRunner;
import be.nabu.glue.api.runs.GlueValidation;
import be.nabu.glue.impl.formatters.MarkdownOutputFormatter;
import be.nabu.glue.impl.formatters.SimpleOutputFormatter;
import be.nabu.glue.utils.ScriptRuntime;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

public class MultithreadedScriptRunner implements ScriptRunner {

	private ExecutorService threadPool;
	private boolean debug;
	private long maxScriptRuntime;
	private OutputFormatterProvider outputFormatterProvider;
	private ScriptRuntimeRunnableWrapper runtimeWrapper;
	private ScriptResultListener resultListener;

	public MultithreadedScriptRunner(int poolSize, long maxScriptRuntime) {
		this(poolSize, maxScriptRuntime, false);
	}
	
	public MultithreadedScriptRunner(int poolSize, long maxScriptRuntime, boolean debug) {
		this(poolSize, maxScriptRuntime, debug, null);
	}
	
	public MultithreadedScriptRunner(int poolSize, long maxScriptRuntime, boolean debug, ThreadFactory threadFactory) {
		this.threadPool = threadFactory == null ? Executors.newFixedThreadPool(poolSize) : Executors.newFixedThreadPool(poolSize, threadFactory);
		this.maxScriptRuntime = maxScriptRuntime;
		this.debug = debug;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ScriptResult> run(ExecutionEnvironment environment, ScriptRepository repository, ScriptFilter filter, LabelEvaluator labelEvaluator) {
		List<ScriptRuntime> runtimes = new ArrayList<ScriptRuntime>();
		Map<Future<?>, ScriptRuntime> futures = new LinkedHashMap<Future<?>, ScriptRuntime>();
		System.out.println("Finding scripts for execution...");
		Map<ScriptRuntime, StringWriter> writers = new HashMap<ScriptRuntime, StringWriter>();
		for (Script script : repository) {
			System.out.print("\t" + script.getName() + " (" + script.getNamespace() + ")...");
			if (filter.accept(script)) {
				ScriptRuntime runtime = new ScriptRuntime(script, environment, debug, new HashMap<String, Object>());
				StringWriter stringWriter = new StringWriter();
				writers.put(runtime, stringWriter);
				if (outputFormatterProvider == null) {
					runtime.setFormatter(new ScriptRunnerFormatter(new MarkdownOutputFormatter(debug ? new MultipleWriter(new OutputStreamWriter(System.out), stringWriter) : stringWriter)));
				}
				else {
					runtime.setFormatter(outputFormatterProvider.newFormatter(new SimpleOutputFormatter(stringWriter)));
				}
//				runtime.setFormatter(new SimpleOutputFormatter(new StringWriter()));
				runtime.setLabelEvaluator(labelEvaluator);
				runtimes.add(runtime);
				System.out.println("accepted");
			}
			else {
				System.out.println("rejected");
			}
		}
		System.out.println("Submitting " + runtimes.size() + " scripts");
		for (ScriptRuntime runtime : runtimes) {
			try {
				futures.put(threadPool.submit(runtimeWrapper == null ? runtime : runtimeWrapper.wrap(runtime)), runtime);
			}
			catch (RejectedExecutionException e) {
				System.out.println("Forced stop submitting due to rejected exception: " + e.getMessage());
				e.printStackTrace();
				break;
			}
		}
		// don't accept anymore incoming tasks
		threadPool.shutdown();
		while(!Thread.interrupted() && !futures.isEmpty()) {
			Iterator<Future<?>> iterator = futures.keySet().iterator();
			while(iterator.hasNext()) {
				Future<?> future = iterator.next();
				ScriptRuntime scriptRuntime = futures.get(future);
				if (future.isDone() || future.isCancelled()) {
					// should merge this with the handling at the end, just making minimal changes now...
					if (resultListener != null) {
						resultListener.handle(toResult(environment, scriptRuntime, writers.get(scriptRuntime).toString()));
					}
					iterator.remove();
				}
				else {
					if (scriptRuntime.getStarted() != null) {
						long runtime = new Date().getTime() - scriptRuntime.getStarted().getTime();
						if (runtime > maxScriptRuntime) {
							synchronized(System.out) {
								System.out.println("Aborting: " + scriptRuntime.getScript().getName() + " (" + scriptRuntime.getScript().getNamespace() + ") because it is running too long: " + runtime + " > " + maxScriptRuntime);
								List<GlueValidation> messages = (List<GlueValidation>) scriptRuntime.getContext().get("$validation");
								if (messages == null) {
									messages = new ArrayList<GlueValidation>();
									scriptRuntime.getContext().put("$validation", messages);
								}
								messages.add(new AbortedValidation(scriptRuntime.getExecutionContext().getCurrent(), runtime, maxScriptRuntime));
								scriptRuntime.abort();
							}
							future.cancel(true);
						}
					}
				}
			}
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!futures.isEmpty()) {
			synchronized(System.out) {
				System.out.println("Preemptive stop of the runner due to thread interrupt, " + futures.size() + " scripts unfinished");
			}
		}
		List<ScriptResult> results = new ArrayList<ScriptResult>();
		for (ScriptRuntime runtime : runtimes) {
			SimpleScriptResult scriptResult = toResult(environment, runtime, writers.get(runtime).toString());
			results.add(scriptResult);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private SimpleScriptResult toResult(ExecutionEnvironment environment, ScriptRuntime runtime, String log) {
		List<GlueValidation> validations  = (List<GlueValidation>) runtime.getContext().get("$validation");
		List<GlueAttachment> attachments = (List<GlueAttachment>) runtime.getContext().get("$attachment");
//			String log = ((MarkdownOutputFormatter) ((ScriptRunnerFormatter) runtime.getFormatter()).getParent()).getWriter().toString();
//		String log;
//		if (outputFormatterProvider != null) {
//			log = ((SimpleOutputFormatter) runtime.getFormatter().getParent()).getWriter().toString();
//		}
//		else {
//			log = ((SimpleOutputFormatter) runtime.getFormatter()).getWriter().toString();
//		}
		return new SimpleScriptResult(environment, runtime.getScript(), runtime.getStarted(), runtime.getStopped(), runtime.getException(), log, validations == null ? new ArrayList<GlueValidation>() : validations, attachments);
	}

	public boolean isDebug() {
		return debug;
	}
	
	public static class AbortedValidation implements GlueValidation {

		private long maxRuntime;
		private long runtime;
		private Date timestamp = new Date();
		private Executor executor;

		public AbortedValidation(Executor executor, long runtime, long maxRuntime) {
			this.executor = executor;
			this.runtime = runtime;
			this.maxRuntime = maxRuntime;
		}
		
		@Override
		public Severity getSeverity() {
			return Severity.ERROR;
		}

		@Override
		public String getMessage() {
			return runtime + " > " + maxRuntime;
		}

		@Override
		public String getDescription() {
			return "The script run timed out";
		}

		@Override
		public List<CallLocation> getContext() {
			return new ArrayList<CallLocation>();
		}

		@Override
		public Executor getExecutor() {
			return executor;
		}

		@Override
		public Date getCreated() {
			return timestamp;
		}

		@Override
		public String getCode() {
			return "0";
		}
		
	}

	public OutputFormatterProvider getOutputFormatterProvider() {
		return outputFormatterProvider;
	}

	public void setOutputFormatterProvider(OutputFormatterProvider outputFormatterProvider) {
		this.outputFormatterProvider = outputFormatterProvider;
	}

	public ScriptRuntimeRunnableWrapper getRuntimeWrapper() {
		return runtimeWrapper;
	}

	public void setRuntimeWrapper(ScriptRuntimeRunnableWrapper runtimeWrapper) {
		this.runtimeWrapper = runtimeWrapper;
	}

	public ScriptResultListener getResultListener() {
		return resultListener;
	}

	public void setResultListener(ScriptResultListener resultListener) {
		this.resultListener = resultListener;
	}

}
