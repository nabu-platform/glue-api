package be.nabu.glue;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.ExecutionException;
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

	public ScriptRuntime(Script script, ExecutionEnvironment environment, boolean debug, Map<String, Object> input) {
		this.script = script;
		this.environment = environment;
		this.debug = debug;
		this.input = input;
	}

	@Override
	public void run() {
		if (runtime.get() != null) {
			parent = runtime.get();
		}
		runtime.set(this);
		try {
			executionContext = new SimpleExecutionContext(environment, labelEvaluator, debug);
			for (String key : input.keySet()) {
				executionContext.getPipeline().put(key, input.get(key));
			}
			if (initialBreakpoint != null) {
				executionContext.setBreakpoint(initialBreakpoint);
			}
			try {
				script.getRoot().execute(executionContext);
			}
			catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
		finally {
			if (getParent() != null) {
				runtime.set(getParent());
			}
			else {
				runtime.remove();
			}
		}
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
