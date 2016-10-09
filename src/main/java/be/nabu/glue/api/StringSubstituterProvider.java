package be.nabu.glue.api;

import be.nabu.glue.utils.ScriptRuntime;

public interface StringSubstituterProvider {
	public StringSubstituter getSubstituter(ScriptRuntime runtime);
}
