package be.nabu.glue.impl.formatters;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import be.nabu.glue.api.Executor;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.Validation;

public class SimpleOutputFormatter implements OutputFormatter {

	private Writer writer;

	public SimpleOutputFormatter(Writer writer) {
		this.writer = writer;
	}
	
	@Override
	public void start(Script script) {
		// do nothing
	}

	@Override
	public void before(Executor executor) {
		// do nothing
	}

	@Override
	public void after(Executor executor) {
		// do nothing		
	}

	@Override
	public void validated(Validation...validations) {
		if (!"true".equals(System.getProperty("hideValidation", "true"))) {
			print(validations);
		}
	}

	@Override
	public void print(Object...messages) {
		if (messages != null) {
			for (Object message : messages) {
				try {
					writer.append(message == null ? "null" : message.toString()).append(System.getProperty("line.separator"));
					writer.flush();
				}
				catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public Writer getWriter() {
		return writer;
	}

	@Override
	public void end(Script script, Date started, Date stopped, Exception exception) {
		// do nothing
	}
}
