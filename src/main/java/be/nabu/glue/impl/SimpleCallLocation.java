package be.nabu.glue.impl;

import be.nabu.glue.api.Executor;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.CallLocation;

public class SimpleCallLocation implements CallLocation {

	private Script script;
	private Executor executor;
	
	public SimpleCallLocation(Script script, Executor executor) {
		this.script = script;
		this.executor = executor;
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}
	
	@Override
	public String toString() {
		String message = script.getName();
		if (executor != null && executor.getContext() != null) {
			message += ":" + (executor.getContext().getLineNumber() + 1);
		}
		return message;
	}

}
