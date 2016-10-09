package be.nabu.glue.impl.formatters;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;

import be.nabu.glue.api.Executor;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.GlueValidation;
import be.nabu.glue.utils.ScriptRuntime;

public class SimpleOutputFormatter implements OutputFormatter {

	private Writer writer;
	private boolean addLineFeeds;
	private boolean replaceVariables;
	private OutputFormatter parent;

	public SimpleOutputFormatter(Writer writer) {
		this(writer, true);
	}
	
	public SimpleOutputFormatter(Writer writer, boolean addLineFeeds) {
		this(writer, addLineFeeds, Boolean.parseBoolean(System.getProperty("output.variables", "true")));
	}
	
	public SimpleOutputFormatter(Writer writer, boolean addLineFeeds, boolean replaceVariables) {
		this.writer = writer;
		this.addLineFeeds = addLineFeeds;
		this.replaceVariables = replaceVariables;
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
					if (message instanceof Object[]) {
						message = Arrays.asList((Object[]) message);
					}
					else if (message instanceof Throwable) {
						StringWriter writer = new StringWriter();
						PrintWriter printer = new PrintWriter(writer);
						((Throwable) message).printStackTrace(printer);
						printer.flush();
						message = writer.toString();
					}
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

	public boolean isReplaceVariables() {
		return replaceVariables;
	}

	public void setReplaceVariables(boolean replaceVariables) {
		this.replaceVariables = replaceVariables;
	}
	
}
