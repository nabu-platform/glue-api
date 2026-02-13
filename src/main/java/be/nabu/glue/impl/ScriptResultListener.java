package be.nabu.glue.impl;

import be.nabu.glue.api.runs.ScriptResult;

public interface ScriptResultListener {
	public void handle(ScriptResult result);
}
