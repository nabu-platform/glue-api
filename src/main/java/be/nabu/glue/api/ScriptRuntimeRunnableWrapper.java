package be.nabu.glue.api;

import be.nabu.glue.utils.ScriptRuntime;

public interface ScriptRuntimeRunnableWrapper {
	public Runnable wrap(ScriptRuntime runtime);
}
