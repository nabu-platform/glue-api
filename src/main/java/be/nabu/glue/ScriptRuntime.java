package be.nabu.glue;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.ExecutionException;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.glue.api.Script;
import be.nabu.glue.impl.SimpleExecutionContext;
import be.nabu.libs.converter.ConverterFactory;
import be.nabu.libs.converter.api.Converter;

public class ScriptRuntime implements Runnable {
	
	private boolean debug;
	private ExecutionEnvironment environment;
	private ExecutionContext executionContext;
	private Script script;
	private Map<String, Object> input;
	private Writer writer;
	private String initialBreakpoint;
	private ScriptRuntime parent;
	private static ThreadLocal<ScriptRuntime> runtime = new ThreadLocal<ScriptRuntime>();
	private Map<String, Object> context;
	private Converter converter = ConverterFactory.getInstance().getConverter();
	private LabelEvaluator labelEvaluator;
	private boolean forked = false;

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
		}
		runtime.set(this);
		try {
			if (executionContext == null) {
				executionContext = new SimpleExecutionContext(environment, labelEvaluator, debug);
				for (String key : input.keySet()) {
					executionContext.getPipeline().put(key, input.get(key));
				}
				if (initialBreakpoint != null) {
					executionContext.setBreakpoint(initialBreakpoint);
				}
			}
			try {
				// preserve the current, mostly important for forking
				Executor current = executionContext.getCurrent();
				script.getRoot().execute(executionContext);
				if (current != null) {
					executionContext.setCurrent(current);
				}
			}
			catch (ExecutionException e) {
				throw new ScriptRuntimeException(this, e);
			}
			catch (IOException e) {
				throw new ScriptRuntimeException(this, e);
			}
			catch (ParseException e) {
				throw new ScriptRuntimeException(this, e);
			}
		}
		finally {
			if (!forked && getParent() != null) {
				runtime.set(getParent());
			}
			else {
				runtime.remove();
			}
		}
	}
	
	public ScriptRuntime fork(Script script) {
		return new ScriptRuntime(this, script);
	}
	
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}
	
	public Writer getWriter() {
		if (writer == null) {
			if (parent != null) {
				writer = parent.getWriter();
			}
			else {
				writer = new OutputStreamWriter(System.out, Charset.forName("UTF-8"));
			}
		}
		return writer;
	}
	
	public Script getScript() {
		return script;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}
	
	public static ScriptRuntime getRuntime() {
		return runtime.get();
	}
	
	public void log(String message) {
		try {
			getWriter().append(message).append(System.getProperty("line.separator"));
			getWriter().flush();
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public ScriptRuntime getParent() {
		return parent;
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

	public String getInitialBreakpoint() {
		return initialBreakpoint;
	}

	public void setInitialBreakpoint(String initialBreakpoint) {
		this.initialBreakpoint = initialBreakpoint;
	}

	public Converter getConverter() {
		return converter;
	}

	public LabelEvaluator getLabelEvaluator() {
		return labelEvaluator == null && parent != null
			? parent.getLabelEvaluator()
			: labelEvaluator;
	}

	public void setLabelEvaluator(LabelEvaluator labelEvaluator) {
		this.labelEvaluator = labelEvaluator;
	}
}
