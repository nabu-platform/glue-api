package be.nabu.glue.api;

public interface AssignmentExecutor extends Executor {
	public boolean isOverwriteIfExists();
	public String getVariableName();
}
