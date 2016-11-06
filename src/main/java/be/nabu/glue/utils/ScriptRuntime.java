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
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.ExecutionException;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.PermissionValidator;
import be.nabu.glue.api.PostProcessor;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.StringSubstituter;
import be.nabu.glue.api.StringSubstituterProvider;
import be.nabu.glue.api.Transactionable;
import be.nabu.glue.impl.ForkedExecutionContext;
import be.nabu.glue.impl.MultipleSubstituter;
import be.nabu.glue.impl.ParserSubstituterProvider;
import be.nabu.glue.impl.SimpleExecutionContext;
import be.nabu.glue.impl.formatters.SimpleOutputFormatter;
import be.nabu.libs.converter.ConverterFactory;
import be.nabu.libs.converter.api.Converter;

public class ScriptRuntime implements Runnable {
	
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
		parent = runtime.get();
		if (!forked && parent != null) {
			parent.child = this;
		}
		runtime.set(this);
		getFormatter().start(script);
		try {
			try {
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
				script.getRoot().execute(executionContext);
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
	
	public ExecutionContext getExecutionContext() {
		return executionContext;
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
		runtime.set(this);
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
	public void abort() {
		getRoot().aborted = true;
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
	
}