package be.nabu.glue.api;

public interface PermissionValidator {
	public boolean canExecute(Script script, ExecutionEnvironment environment);
}
