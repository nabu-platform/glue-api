package be.nabu.glue.api;

import java.util.List;

public interface ExecutorGroup extends Executor {
	public List<Executor> getChildren();
}
