package be.nabu.glue.impl.formatters;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.Date;

import be.nabu.glue.api.AssignmentExecutor;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.Validation;

public class MarkdownOutputFormatter extends SimpleOutputFormatter {
	
	private int depth = 2;
	private Script root;
	private int scriptDepth = 0;
	private boolean inValidation = false, inBlock = false;
	
	public MarkdownOutputFormatter(Writer writer) {
		super(writer);
	}

	@Override
	public void start(Script script) {
		if (root == null) {
			root = script;
			try {
				String title = script.getRoot().getContext().getAnnotations().get("title");
				if (title == null) {
					title = script.getName();
				}
				printBlock("# " + title, "");
				if (script.getRoot().getContext().getDescription() != null) {
					printBlock(script.getRoot().getContext().getDescription(), "");
				}
			}
			catch (ParseException e) {
				throw new RuntimeException(e);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			scriptDepth++;
		}
	}

	@Override
	public void before(Executor executor) {
		if (scriptDepth == 0 && executor instanceof ExecutorGroup && executor.getContext().getDescription() != null) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < depth; i++) {
				builder.append("#");
			}
			builder.append(" ");
			builder.append(executor.getContext().getDescription());
			printBlock(builder.toString(), "");
			depth++;
		}
		else if (scriptDepth == 0 && executor.getContext().getDescription() != null) {
			// you can also set comments on inputs clarifying what should be in them, we should not echo this
			if (!(executor instanceof AssignmentExecutor) || ((AssignmentExecutor) executor).isOverwriteIfExists()) {
				print(executor.getContext().getDescription());
			}
		}
	}

	@Override
	public void after(Executor executor) {
		if (scriptDepth == 0 && executor instanceof ExecutorGroup && executor.getContext().getDescription() != null) {
			depth--;
		}
	}

	@Override
	public void validated(Validation...validations) {
		if (inBlock) {
			inBlock = false;
			super.print("");
		}
		inValidation = true;
		for (Validation validation : validations) {
			super.print("- " + validation);
		}
	}
	
	public void printBlock(Object...messages) {
		if (inBlock) {
			inBlock = false;
			super.print("");
		}
		if (inValidation) {
			super.print("");
			inValidation = false;
		}
		super.print(messages);
	}
	
	@Override
	public void print(Object...messages) {
		// still building validations, want to end the list
		if (inValidation) {
			super.print("");
			inValidation = false;
		}
		inBlock = true;
		super.print(messages);
	}

	@Override
	public void end(Script script, Date started, Date stopped, Exception exception) {
		scriptDepth--;
		if (exception != null) {
			StringWriter output = new StringWriter();
			PrintWriter writer = new PrintWriter(output);
			exception.printStackTrace(writer);
			writer.flush();
			print(output.toString());
		}
	}
}
