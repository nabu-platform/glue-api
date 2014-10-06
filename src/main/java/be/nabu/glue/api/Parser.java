package be.nabu.glue.api;

import java.io.IOException;
import java.text.ParseException;

import be.nabu.utils.io.api.CharBuffer;
import be.nabu.utils.io.api.ReadableContainer;

public interface Parser {
	public ExecutorGroup parse(ReadableContainer<CharBuffer> content) throws IOException, ParseException;
	public String substitute(String value, ExecutionContext context);
}
