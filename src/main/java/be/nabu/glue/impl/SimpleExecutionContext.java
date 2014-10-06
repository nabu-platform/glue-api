package be.nabu.glue.impl;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import be.nabu.glue.ScriptRuntime;
import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.libs.resources.ResourceReadableContainer;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;

public class SimpleExecutionContext implements ExecutionContext {

	private Map<String, Object> contextVariables = new LinkedHashMap<String, Object>();
	private ExecutionEnvironment executionEnvironment;
	private boolean debug;
	private Executor current;
	private String breakpoint;
	private LabelEvaluator labelEvaluator;

	public SimpleExecutionContext(ExecutionEnvironment executionEnvironment, LabelEvaluator labelEvaluator, boolean debug) {
		this.executionEnvironment = executionEnvironment;
		this.labelEvaluator = labelEvaluator;
		this.debug = debug;
	}

	@Override
	public ExecutionEnvironment getExecutionEnvironment() {
		return this.executionEnvironment;
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
	
	private String toString(int offset) {
		StringBuilder builder = new StringBuilder();
		for (String key : contextVariables.keySet()) {
			pad(builder, offset);
			builder.append(key).append(" = ");
			if (contextVariables.get(key) instanceof SimpleExecutionContext) {
				builder.append("{")
					.append(System.getProperty("line.separator"))
					.append(((SimpleExecutionContext) contextVariables.get(key)).toString(offset + 1));
				pad(builder, offset);
				builder.append("}");
			}
			else if (contextVariables.get(key) instanceof ExecutionContext) {
				builder.append("{ ").append(contextVariables.get(key)).append(" }");
			}
			else {
				builder.append(contextVariables.get(key));
			}
			builder.append(System.getProperty("line.separator"));
		}
		return builder.toString();
	}
	private void pad(StringBuilder builder, int offset) {
		for (int i = 0; i < offset; i++) {
			builder.append("\t");
		}
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	@Override
	public Executor getCurrent() {
		return current;
	}

	@Override
	public void setCurrent(Executor current) {
		if (debug && current != null) {
			ScriptRuntime.getRuntime().log("Line " + (current.getContext().getLineNumber() + 1) + ": " + current.getContext().getLine());
		}
		this.current = current;
	}

	@Override
	public String getBreakpoint() {
		return breakpoint;
	}

	@Override
	public void setBreakpoint(String breakpoint) {
		this.breakpoint = breakpoint;
	}

	@Override
	public ReadableContainer<ByteBuffer> getContent(String name) throws IOException {
		ScriptRuntime runtime = ScriptRuntime.getRuntime();
		while (runtime != null) {
			Resource resource = runtime.getScript().getResources().getChild(name);
			if (resource != null) {
				return new ResourceReadableContainer((ReadableResource) resource);
			}
			runtime = runtime.getParent();
		}
		return null;
	}

	@Override
	public LabelEvaluator getLabelEvaluator() {
		return labelEvaluator;
	}

	@Override
	public Map<String, Object> getPipeline() {
		return contextVariables;
	}
}
