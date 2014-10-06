package be.nabu.glue.api;

public interface LabelEvaluator {
	public boolean shouldExecute(String label, ExecutionEnvironment environment);
}
