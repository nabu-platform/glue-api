package be.nabu.glue.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import be.nabu.glue.ScriptRuntime;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptFilter;
import be.nabu.glue.api.ScriptRepository;
import be.nabu.glue.api.runs.ScriptResult;
import be.nabu.glue.api.runs.ScriptRunner;
import be.nabu.glue.api.runs.Validation;
import be.nabu.glue.impl.formatters.MarkdownOutputFormatter;

public class MultithreadedScriptRunner implements ScriptRunner {

	private ExecutorService threadPool;

	public MultithreadedScriptRunner(int poolSize) {
		this.threadPool = Executors.newFixedThreadPool(poolSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScriptResult> run(ExecutionEnvironment environment, ScriptRepository repository, ScriptFilter filter, LabelEvaluator labelEvaluator) {
		List<ScriptRuntime> runtimes = new ArrayList<ScriptRuntime>();
		for (Script script : repository) {
			if (filter.accept(script)) {
				ScriptRuntime runtime = new ScriptRuntime(script, environment, false, new HashMap<String, Object>());
				runtime.setFormatter(new ScriptRunnerFormatter(new MarkdownOutputFormatter(new StringWriter())));
				runtime.setLabelEvaluator(labelEvaluator);
				runtimes.add(runtime);
				threadPool.execute(runtime);
			}
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(24, TimeUnit.HOURS);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		List<ScriptResult> results = new ArrayList<ScriptResult>();
		for (ScriptRuntime runtime : runtimes) {
			List<Validation> validations  = (List<Validation>) runtime.getContext().get("$validation");
			results.add(new SimpleScriptResult(environment, runtime.getScript(), runtime.getStarted(), runtime.getStopped(), runtime.getException(), ((StringWriter) ((MarkdownOutputFormatter) ((ScriptRunnerFormatter) runtime.getFormatter()).getParent()).getWriter()).toString(), validations == null ? new ArrayList<Validation>() : validations));
		}
		return results;
	}
}
