package be.nabu.glue;

import be.nabu.glue.api.Executor;

public class ScriptRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = -4284853327836193254L;
	private ScriptRuntime runtime;
	private String message;

	public ScriptRuntimeException(ScriptRuntime runtime, Throwable cause) {
		super(cause);
		this.runtime = runtime;
	}
	
	public ScriptRuntimeException(ScriptRuntime runtime, String message) {
		super(message);
		this.runtime = runtime;
		this.message = message;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		if (runtime == null || runtime.getScript() == null) {
			builder.append("Unknown error");
		}
		else {
			builder.append("Error occurred in " + runtime.getScript().getName());
		}
		if (runtime.getExecutionContext().getCurrent() != null) {
			Executor executor = runtime.getExecutionContext().getCurrent();
			builder.append(" at line " + (executor.getContext().getLineNumber() + 1) + ": " + executor.getContext().getLine());
		}
		if (message != null) {
			builder.append(": " + message);
		}
		return builder.toString();
	}
	
}
