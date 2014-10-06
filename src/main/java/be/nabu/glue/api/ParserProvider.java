package be.nabu.glue.api;

public interface ParserProvider {
	public Parser newParser(ScriptRepository scriptRepository, String name);
}
