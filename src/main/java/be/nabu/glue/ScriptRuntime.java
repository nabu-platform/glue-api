package be.nabu.glue;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.ExecutionException;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.Script;
import be.nabu.glue.impl.SimpleExecutionContext;
import be.nabu.glue.impl.formatters.SimpleOutputFormatter;
import be.nabu.libs.converter.ConverterFactory;
import be.nabu.libs.converter.api.Converter;

public class ScriptRuntime implements Runnable {
	
	private boolean debug, trace;
	private ExecutionEnvironment environment;
	private ExecutionContext executionContext;
	private Script script;
	private Map<String, Object> input;
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

	public ScriptRuntime(Script script, ExecutionEnvironment environment, boolean debug, Map<String, Object> input) {
		this.script = script;
		this.environment = environment;
		this.debug = debug;
		this.input = input;
	}
	
	private ScriptRuntime(ScriptRuntime parent, Script script) {
		this.parent = parent;
		this.script = script;
		this.environment = parent.environment;
		this.debug = parent.debug;
		this.executionContext = parent.getExecutionContext();
		this.forked = true;
	}

	@Override
	public void run() {
		if (!forked && runtime.get() != null) {
			parent = runtime.get();
			parent.child = this;
		}
		runtime.set(this);
		getFormatter().start(script);
		try {
			if (executionContext == null) {
				executionContext = new SimpleExecutionContext(environment, getLabelEvaluator(), debug);
				if (input != null) {
					for (String key : input.keySet()) {
						executionContext.getPipeline().put(key, input.get(key));
					}
				}
			}
			try {
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
			if (!forked && getParent() != null) {
				parent.child = null;
				runtime.set(getParent());
			}
			else {
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

	public ScriptRuntime fork(Script script) {
		return new ScriptRuntime(this, script);
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
				formatter = new SimpleOutputFormatter(new OutputStreamWriter(System.out, Charset.forName("UTF-8")));
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
}
