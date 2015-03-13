package be.nabu.glue.api.runs;

import be.nabu.glue.api.Executor;
import be.nabu.glue.api.Script;

public interface CallLocation {
	public Script getScript();
	public Executor getExecutor();
}
