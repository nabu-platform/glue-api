package be.nabu.glue;

import be.nabu.glue.api.Executor;

public class ScriptRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = -4284853327836193254L;
	private ScriptRuntime runtime;

	public ScriptRuntimeException(ScriptRuntime runtime, Throwable cause) {
		super(cause);
		this.runtime = runtime;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("Error occurred in " + runtime.getScript().getName());
		if (runtime.getExecutionContext().getCurrent() != null) {
			Executor executor = runtime.getExecutionContext().getCurrent();
			builder.append(" at line " + executor.getContext().getLineNumber() + ": " + executor.getContext().getLine());
		}
		return builder.toString();
	}
	
}
