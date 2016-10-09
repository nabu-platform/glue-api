package be.nabu.glue.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.glue.utils.ScriptRuntime;

public class SimpleExecutionContext implements ExecutionContext {

	private Map<String, Object> contextVariables = new LinkedHashMap<String, Object>();
	private ExecutionEnvironment executionEnvironment;
	private boolean debug, trace;
	private Executor current;
	private Set<String> breakpoints = new HashSet<String>();
	private LabelEvaluator labelEvaluator;
	private int breakCount;
	private Principal principal;
	private boolean outputCurrentLine = true;

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
		if (outputCurrentLine && debug && current != null) {
			ScriptRuntime.getRuntime().getFormatter().print("[" + ScriptRuntime.getRuntime().getScript().getName() + "] Line " + (current.getContext().getLineNumber() + 1) + ": " + current.getContext().getLine());
		}
		this.current = current;
	}

	@Override
	public Set<String> getBreakpoints() {
		return breakpoints;
	}

	@Override
	public void addBreakpoint(String...breakpoint) {
		this.breakpoints.addAll(Arrays.asList(breakpoint));
	}

	@Override
	public InputStream getContent(String name) throws IOException {
		ScriptRuntime runtime = ScriptRuntime.getRuntime();
		while (runtime != null) {
			InputStream input = runtime.getScript().getResource(name);
			if (input != null) {
				return input;
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

	@Override
	public void removeBreakpoint(String id) {
		breakpoints.remove(id);
	}

	@Override
	public boolean isTrace() {
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public void removeBreakpoints() {
		breakpoints.clear();
	}

	@Override
	public int getBreakCount() {
		return breakCount;
	}

	@Override
	public void incrementBreakCount(int breakCount) {
		this.breakCount += breakCount;
	}

	@Override
	public Principal getPrincipal() {
		return principal;
	}
	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public boolean isOutputCurrentLine() {
		return outputCurrentLine;
	}

	public void setOutputCurrentLine(boolean outputCurrentLine) {
		this.outputCurrentLine = outputCurrentLine;
	}
}
