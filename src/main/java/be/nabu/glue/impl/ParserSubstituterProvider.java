package be.nabu.glue.impl;

import be.nabu.glue.api.StringSubstituter;
import be.nabu.glue.api.StringSubstituterProvider;
import be.nabu.glue.utils.ScriptRuntime;

public class ParserSubstituterProvider implements StringSubstituterProvider {
	@Override
	public StringSubstituter getSubstituter(ScriptRuntime runtime) {
		return runtime.getScript().getParser();
	}
}
