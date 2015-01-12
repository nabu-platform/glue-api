package be.nabu.glue.api;

import java.io.IOException;
import java.text.ParseException;

public interface ScriptRepository extends Iterable<Script> {
	public Script getScript(String name) throws IOException, ParseException;
	public ParserProvider getParserProvider();
	public ScriptRepository getParent();
	public void refresh() throws IOException;
}