package be.nabu.glue.impl.formatters;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import be.nabu.glue.ScriptRuntime;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.GlueValidation;

public class SimpleOutputFormatter implements OutputFormatter {

	private Writer writer;
	private boolean addLineFeeds;
	private boolean replaceVariables = Boolean.parseBoolean(System.getProperty("output.variables", "true"));
	private OutputFormatter parent;

	public SimpleOutputFormatter(Writer writer) {
		this(writer, true);
	}
	
	public SimpleOutputFormatter(Writer writer, boolean addLineFeeds) {
		this.writer = writer;
		this.addLineFeeds = addLineFeeds;
	}
	
	@Override
	public void start(Script script) {
		// do nothing
	}

	@Override
	public void before(Executor executor) {
		if (parent != null) {
			parent.before(executor);
		}
	}

	@Override
	public void after(Executor executor) {
		if (parent != null) {
			parent.after(executor);
		}		
	}

	@Override
	public void validated(GlueValidation...validations) {
		if (!"true".equals(System.getProperty("hideValidation", "true"))) {
			print((Object[]) validations);
		}
	}

	@Override
	public void print(Object...messages) {
		if (messages != null) {
			for (Object message : messages) {
				try {
					String content = message == null ? "null" : message.toString();
					if (replaceVariables) {
						ScriptRuntime runtime = ScriptRuntime.getRuntime();
						if (runtime != null && runtime.getExecutionContext() != null && runtime.getScript() != null && runtime.getScript().getParser() != null) {
							content = runtime.getSubstituter().substitute(content, runtime.getExecutionContext(), true);
						}
					}
					writer.append(content);
					if (addLineFeeds) {
						writer.append(System.getProperty("line.separator"));
					}
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

	@Override
	public boolean shouldExecute(Executor executor) {
		return parent == null || parent.shouldExecute(executor);
	}

	public OutputFormatter getParent() {
		return parent;
	}

	public void setParent(OutputFormatter parent) {
		this.parent = parent;
	}
}
