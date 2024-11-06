/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.GlueValidation;
import be.nabu.glue.utils.ScriptUtils;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

public class MarkdownOutputFormatter extends SimpleOutputFormatter {
	
	private OutputFormatter parent;
	private boolean allowDeepLogging = true;
	private int depth = 2;
	private Script root;
	private int scriptDepth = 0;
	private boolean inValidation = false, inBlock = false;
	private String validationGroup;
	private boolean includeBuildTimestamp = Boolean.parseBoolean(System.getProperty("glue.include-build", "true"));
	
	public MarkdownOutputFormatter(Writer writer) {
		super(writer);
	}

	@Override
	public void start(Script script) {
		if (root == null) {
			root = script;
			if (includeBuildTimestamp && ScriptUtils.getBuildTime().getTime() != 0) {
				printBlock("@build " + ScriptUtils.getBuildTime(), "");
			}
			try {
				String title = script.getRoot().getContext() == null ? null : script.getRoot().getContext().getAnnotations().get("title");
				if (title == null) {
					title = script.getName();
				}
				printBlock("# " + title, "");
				if (script.getRoot().getContext() != null && script.getRoot().getContext().getDescription() != null) {
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
		else if (!allowDeepLogging) {
			scriptDepth++;
		}
		if (parent != null) {
			parent.start(script);
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
		if (parent != null) {
			parent.before(executor);
		}
	}

	@Override
	public void after(Executor executor) {
		if (scriptDepth == 0 && executor instanceof ExecutorGroup && executor.getContext().getDescription() != null) {
			depth--;
		}
		if (parent != null) {
			parent.after(executor);
		}
	}

	@Override
	public void validated(GlueValidation...validations) {
		if (inBlock) {
			inBlock = false;
			super.print("");
		}
		inValidation = true;
		for (GlueValidation validation : validations) {
			boolean grouped = false;
			String validationGroup = validation.getExecutor().getContext() != null && validation.getExecutor().getContext().getAnnotations() != null 
				? validation.getExecutor().getContext().getAnnotations().get("group")
				: null;
			if (validationGroup != null) {
				grouped = true;
				// a new group, print the name
				if (!validationGroup.equals(this.validationGroup)) {
					super.print("- " + validationGroup);
					this.validationGroup = validationGroup;
				}
			}
			else if (this.validationGroup != null) {
				this.validationGroup = null;
			}
			if (validation.getSeverity() == Severity.ERROR || validation.getSeverity() == Severity.CRITICAL) {
				super.print((grouped ? "\t" : "") + "- !!" + validation + "!!");
			}
			else {
				super.print((grouped ? "\t" : "") + "- " + validation);
			}
		}
		if (parent != null) {
			parent.validated(validations);
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
		if (parent != null) {
			parent.print(messages);
		}
	}

	@Override
	public void end(Script script, Date started, Date stopped, Exception exception) {
		if (!allowDeepLogging) {
			scriptDepth--;
		}
		if (exception != null) {
			StringWriter output = new StringWriter();
			PrintWriter writer = new PrintWriter(output);
			exception.printStackTrace(writer);
			writer.flush();
			print(output.toString());
		}
		if (parent != null) {
			parent.end(script, started, stopped, exception);
		}
	}

	public boolean isAllowDeepLogging() {
		return allowDeepLogging;
	}
	public void setAllowDeepLogging(boolean allowDeepLogging) {
		this.allowDeepLogging = allowDeepLogging;
	}

	public OutputFormatter getParent() {
		return parent;
	}
	public void setParent(OutputFormatter parent) {
		this.parent = parent;
	}
}
