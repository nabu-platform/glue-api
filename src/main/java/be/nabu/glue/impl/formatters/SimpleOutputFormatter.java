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
	private boolean outputted;

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

	// WARNING @2023-06-15: at some point I added to both printing & validating that it should push this to the parent as well
	// but this breaks glue-based templating during resolve in for example javascript files that have embedded glue syntax
	// the result is that the output appears twice: where it is supposed to and before (or after) as well
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
					outputted = true;
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

	public boolean isOutputted() {
		return outputted;
	}
	
}
