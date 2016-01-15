package be.nabu.glue.api;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public interface Parser extends StringSubstituter {
	public ExecutorGroup parse(Reader content) throws IOException, ParseException;
}
