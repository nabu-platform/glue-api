package be.nabu.glue.api;

public interface ScriptRewriter {
	public ExecutorGroup rewrite(ScriptRepository repository, ExecutorGroup root);
}
