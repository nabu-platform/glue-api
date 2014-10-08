package be.nabu.glue.api;

import java.io.IOException;
import java.io.Writer;

public interface Formatter {
	public void format(ExecutorGroup group, Writer writer) throws IOException;
}
