package be.nabu.glue.api;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public interface Parser {
	public ExecutorGroup parse(Reader content) throws IOException, ParseException;
	public String substitute(String value, ExecutionContext context, boolean allowNull);
}
