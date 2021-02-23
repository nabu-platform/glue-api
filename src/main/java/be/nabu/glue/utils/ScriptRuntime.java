package be.nabu.glue.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.ExecutionException;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.InputProvider;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.PermissionValidator;
import be.nabu.glue.api.PostProcessor;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.StreamProvider;
import be.nabu.glue.api.StringSubstituter;
import be.nabu.glue.api.StringSubstituterProvider;
import be.nabu.glue.api.Transactionable;
import be.nabu.glue.impl.ForkedExecutionContext;
import be.nabu.glue.impl.MultipleSubstituter;
import be.nabu.glue.impl.ParserSubstituterProvider;
import be.nabu.glue.impl.SimpleExecutionContext;
import be.nabu.glue.impl.StandardInputProvider;
import be.nabu.glue.impl.formatters.SimpleOutputFormatter;
import be.nabu.libs.converter.ConverterFactory;
import be.nabu.libs.converter.api.Converter;
import be.nabu.libs.metrics.api.MetricInstance;
import be.nabu.libs.metrics.api.MetricProvider;
import be.nabu.libs.metrics.api.MetricTimer;

public class ScriptRuntime implements Runnable {
	
	public static final String METRIC_EXECUTION_TIME = "scriptExecutionTime";
	// by default sandboxed scripts can only last 15 seconds
	public static final Long SANDBOX_DURATION = Long.parseLong(System.getProperty("sandbox.duration", "15000"));
	
	private boolean debug, trace;
	private ExecutionEnvironment environment;
	private ExecutionContext executionContext;
	private Script script;
	private Set<String> breakpoints = new HashSet<String>();
	private ScriptRuntime parent;
	private ScriptRuntime child;
	private static ThreadLocal<ScriptRuntime> runtime = new ThreadLocal<ScriptRuntime>();
	private Map<String, Object> context;
	private Converter converter = ConverterFactory.getInstance().getConverter();
	private LabelEvaluator labelEvaluator;
	private boolean forked = false;
	private Date started, stopped;
	private Exception exception;
	private OutputFormatter formatter;
	private InputProvider inputProvider;
	private StreamProvider streamProvider;
	private boolean aborted = false;
	private List<Transactionable> transactionables = new ArrayList<Transactionable>();
	private PermissionValidator permissionValidator;
	private List<StringSubstituterProvider> substituterProviders;
	private List<PostProcessor> postProcessors;

	public ScriptRuntime(Script script, ExecutionContext context, Map<String, Object> input) {
		this.script = script;
		this.environment = context.getExecutionEnvironment();
		this.debug = context.isDebug();
		this.executionContext = context;
		if (input != null) {
			for (String key : input.keySet()) {
				executionContext.getPipeline().put(key, input.get(key));
			}
		}
	}
	
	public ScriptRuntime(Script script, ExecutionEnvironment environment, boolean debug, Map<String, Object> input) {
		this.script = script;
		this.environment = environment;
		this.debug = debug;
		this.executionContext = new SimpleExecutionContext(environment, getLabelEvaluator(), debug);
		if (input != null) {
			for (String key : input.keySet()) {
				executionContext.getPipeline().put(key, input.get(key));
			}
		}
	}
	
	private ScriptRuntime(ScriptRuntime parent, Script script, boolean localPipeline) {
		this.parent = parent;
		this.script = script;
		this.environment = parent.environment;
		this.debug = parent.debug;
		this.trace = parent.trace;
		this.executionContext = new ForkedExecutionContext(parent.getExecutionContext(), localPipeline);
		this.forked = true;
	}

	@Override
	public void run() {
		// in sandbox mode, if it is the root of a script, we will abort it after a set time interval
		boolean sandboxed = "true".equals(executionContext.getExecutionEnvironment().getParameters().get("sandboxed"));
		ForkJoinTask<?> aborter = null;
		if (sandboxed && SANDBOX_DURATION > 0 && (forked || parent == null)) {
			aborter = ForkJoinPool.commonPool().submit(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(SANDBOX_DURATION);
					}
					catch (InterruptedException e) {
						// ignore interruption
					}
					abort();
				}
			});
		}
		
		parent = runtime.get();
		if (!forked && parent != null) {
			parent.child = this;
		}
		runtime.set(this);
		try {
			getFormatter().start(script);
			try {
				try {
					MetricInstance metrics = executionContext instanceof MetricProvider ? ((MetricProvider) executionContext).getMetricInstance(ScriptUtils.getFullName(script)) : null;
					if (permissionValidator != null && !permissionValidator.canExecute(script, environment)) {
						throw new ScriptRuntimeException(this, "No permission to execute script: " + script.getName());
					}
					if (trace) {
						scanForBreakpoints(script.getRoot());
						executionContext.addBreakpoint(breakpoints.toArray(new String[breakpoints.size()]));
					}
					// preserve the current, mostly important for forking
					Executor current = executionContext.getCurrent();
					// make sure we detect breakpoints if we are tracing
					executionContext.setTrace(trace);
					started = new Date();
					MetricTimer timer = metrics == null ? null : metrics.start(METRIC_EXECUTION_TIME);
					script.getRoot().execute(executionContext);
					if (timer != null) {
						timer.stop();
					}
					stopped = new Date();
					if (postProcessors != null) {
						for (PostProcessor processor : postProcessors) {
							processor.postProcess(executionContext);
						}
					}
					trace = executionContext.isTrace();
					if (current != null) {
						executionContext.setCurrent(current);
					}
				}
				catch (ScriptRuntimeException e) {
					exception = e;
					throw e;
				}
				catch (ExecutionException e) {
					exception = e;
					throw new ScriptRuntimeException(this, e);
				}
				catch (IOException e) {
					exception = e;
					throw new ScriptRuntimeException(this, e);
				}
				catch (ParseException e) {
					exception = e;
					throw new ScriptRuntimeException(this, e);
				}
				catch (RuntimeException e) {
					exception = e;
					throw new ScriptRuntimeException(this, e);
				}
			}
			finally {
				getFormatter().end(script, started, stopped, exception);
			}
		}
		finally {
			// if we have exited the root script, cancel any timer that might exist
			if (aborter != null) {
				aborter.cancel(true);
			}
			if (getParent() != null) {
				if (!forked) {
					parent.child = null;
				}
				runtime.set(getParent());
			}
			else {
				// if there is no parent left, finish all the transactionables
				for (Transactionable transactionable : getTransactionables()) {
					try {
						if (exception == null) {
							transactionable.commit();
						}
						else {
							transactionable.rollback();
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				runtime.remove();
			}
		}
	}
	
	private void scanForBreakpoints(ExecutorGroup root) {
		for (Executor child : root.getChildren()) {
			if (child.getContext().getAnnotations().containsKey("breakpoint")) {
				breakpoints.add(child.getId());
			}
			if (child instanceof ExecutorGroup) {
				scanForBreakpoints((ExecutorGroup) child);
			}
		}
	}

	public ScriptRuntime fork(boolean localPipeline) {
		return new ScriptRuntime(this, script, localPipeline);
	}
	
	public ScriptRuntime fork(Script script) {
		return new ScriptRuntime(this, script, false);
	}
	
	public ScriptRuntime fork(Script script, boolean localPipeline) {
		return new ScriptRuntime(this, script, localPipeline);
	}
	
	public ScriptRuntime fork(ExecutionContext context) {
		ScriptRuntime runtime = new ScriptRuntime(script, context, new HashMap<String, Object>());
		runtime.setFormatter(getFormatter());
		runtime.setLabelEvaluator(getLabelEvaluator());
		runtime.setPermissionValidator(getPermissionValidator());
		runtime.setPostProcessors(getPostProcessors());
		runtime.setTrace(isTrace());
		return runtime;
	}
	
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}
	
	public StreamProvider getStreamProvider() {
		if (streamProvider == null && parent != null) {
			return parent.getStreamProvider();
		}
		return streamProvider;
	}

	public void setStreamProvider(StreamProvider streamProvider) {
		this.streamProvider = streamProvider;
	}

	public InputProvider getInputProvider() {
		if (inputProvider == null) {
			if (parent != null) {
				inputProvider = parent.getInputProvider();
			}
			else {
				inputProvider = new StandardInputProvider();
			}
		}
		return inputProvider;
	}

	public void setInputProvider(InputProvider inputProvider) {
		this.inputProvider = inputProvider;
	}

	public void setFormatter(OutputFormatter formatter) {
		this.formatter = formatter;
	}
	
	public OutputFormatter getFormatter() {
		if (formatter == null) {
			if (parent != null) {
				formatter = parent.getFormatter();
			}
			else {
				formatter = new SimpleOutputFormatter(new OutputStreamWriter(System.out, Charset.forName("UTF-8")), true, false);
			}
		}
		return formatter;
	}
	
	public Script getScript() {
		return script;
	}
	
	public static ScriptRuntime getRuntime() {
		return runtime.get();
	}
	
	public ScriptRuntime getParent() {
		return parent;
	}
	
	public ScriptRuntime getChild() {
		return child;
	}

	public Map<String, Object> getContext() {
		if (parent != null) {
			return parent.getContext();
		}
		else if (context == null) {
			context = new HashMap<String, Object>();
		}
		return context;
	}

	public Set<String> getBreakpoints() {
		return breakpoints;
	}
	
	public void addBreakpoint(String...breakpoints) {
		this.breakpoints.addAll(Arrays.asList(breakpoints));
	}

	public void removeBreakpoint(String breakpoint) {
		this.breakpoints.remove(breakpoint);
	}
	
	public void removeBreakpoints() {
		this.breakpoints.clear();
	}
	
	public Converter getConverter() {
		return converter;
	}

	private LabelEvaluator getLabelEvaluator() {
		return labelEvaluator == null && parent != null
			? parent.getLabelEvaluator()
			: labelEvaluator;
	}

	public void setLabelEvaluator(LabelEvaluator labelEvaluator) {
		this.labelEvaluator = labelEvaluator;
	}
	
	public boolean hasRun() {
		return started != null;
	}
	
	public long getDuration() {
		return stopped != null ? stopped.getTime() - started.getTime() : 0;
	}

	public boolean isTrace() {
		return trace;
	}
	
	public Date getStarted() {
		return started;
	}

	public Date getStopped() {
		return stopped;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public Exception getException() {
		return exception;
	}
	
	public ScriptRuntime getRoot() {
		ScriptRuntime current = this;
		while (current.getParent() != null) {
			current = current.getParent();
		}
		return current;
	}
	
	public void registerInThread() {
		registerInThread(false);
	}
	
	public void registerInThread(boolean inheritParent) {
		if (runtime.get() != null && inheritParent) {
			parent = runtime.get();
			parent.child = this;
		}
		runtime.set(this);
	}
	
	public void unregisterInThread() {
		if (runtime.get() != null && runtime.get().equals(this)) {
			runtime.set(null);
		}
		if (parent != null && equals(parent.child)) {
			parent.child = null;
		}
	}
	
	public void unregisterAnyInThread() {
		if (runtime.get() != null) {
			runtime.set(null);
		}
	}

	public boolean isAborted() {
		return aborted || getRoot().aborted;
	}
	
	public void abort(boolean locally) {
		if (locally) {
			aborted = true;
		}
		else {
			abort();
		}
	}
	@SuppressWarnings("unchecked")
	public void abort() {
		getRoot().aborted = true;
		// if we abort, shut down all the pending futures too
		List<Future<?>> futures = (List<Future<?>>) getContext().get("futures");
		if (futures != null) {
			for (Future<?> future : futures) {
				if (!future.isDone() && !future.isDone()) {
					future.cancel(true);
				}
			}
		}
	}
	
	List<Transactionable> getTransactionables() {
		if (parent != null) {
			return parent.getTransactionables();
		}
		return transactionables;
	}
	
	public void addTransactionable(Transactionable transactionable) {
		if (!getTransactionables().contains(transactionable)) {
			getTransactionables().add(transactionable);
		}
	}
	
	public void removeTransactionable(Transactionable transactionable) {
		getTransactionables().remove(transactionable);
	}

	public PermissionValidator getPermissionValidator() {
		return permissionValidator == null && parent != null ? parent.getPermissionValidator() : permissionValidator;
	}

	public void setPermissionValidator(PermissionValidator permissionValidator) {
		this.permissionValidator = permissionValidator;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public void addSubstituterProviders(Collection<StringSubstituterProvider> providers) {
		if (substituterProviders == null) {
			substituterProviders = new ArrayList<StringSubstituterProvider>();
			if (parent != null) {
				substituterProviders.addAll(parent.getSubstituterProviders());
			}
		}
		substituterProviders.addAll(providers);
	}
	
	private List<StringSubstituterProvider> getSubstituterProviders() {
		if (substituterProviders == null && parent != null) {
			return parent.getSubstituterProviders();
		}
		else if (substituterProviders == null) {
			substituterProviders = new ArrayList<StringSubstituterProvider>();
		}
		return substituterProviders;
	}
	
	public StringSubstituter getSubstituter() {
		List<StringSubstituter> substituters = new ArrayList<StringSubstituter>();
		for (StringSubstituterProvider provider : getSubstituterProviders()) {
			StringSubstituter substituter = provider.getSubstituter(this);
			if (substituter != null) {
				substituters.add(substituter);
			}
		}
		// for legacy reasons this always has to be there, have it last so you can generate code to execute
		StringSubstituter scriptSubstituter = new ParserSubstituterProvider().getSubstituter(this);
		if (scriptSubstituter != null) {
			substituters.add(scriptSubstituter);
		}
		return new MultipleSubstituter(substituters);
	}

	public List<PostProcessor> getPostProcessors() {
		return postProcessors;
	}
	public void setPostProcessors(List<PostProcessor> postProcessors) {
		this.postProcessors = postProcessors;
	}
	
	@SuppressWarnings("unchecked")
	public List<Future<?>> getFutures() {
		List<Future<?>> futures = (List<Future<?>>) getContext().get("futures");
		if (futures == null) {
			synchronized(getContext()) {
				futures = (List<Future<?>>) getContext().get("futures");
				if (futures == null) {
					futures = new ArrayList<Future<?>>();
					getContext().put("futures", futures);
				}
			}
		}
		return futures;
	}
	
	public void addFuture(Future<?>...newFutures) {
		List<Future<?>> futures = getFutures();
		synchronized(futures) {
			Iterator<Future<?>> iterator = futures.iterator();
			while(iterator.hasNext()) {
				Future<?> future = iterator.next();
				if (future.isDone() || future.isCancelled()) {
					iterator.remove();
				}
			}
			futures.addAll(Arrays.asList(newFutures));
		}
	}
	
	private List<String> imports = new ArrayList<String>();
	
	public List<String> getImports() {
		return imports;
	}
	
	
}
