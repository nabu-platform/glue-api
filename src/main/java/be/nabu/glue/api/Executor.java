package be.nabu.glue.api;

public interface Executor {
	public boolean shouldExecute(ExecutionContext context) throws ExecutionException;
	public void execute(ExecutionContext context) throws ExecutionException;
	public ExecutorContext getContext();
	public String getId();
	public ExecutorGroup getParent();
}
