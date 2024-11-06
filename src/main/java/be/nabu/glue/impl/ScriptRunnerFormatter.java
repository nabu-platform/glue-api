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

package be.nabu.glue.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.nabu.glue.api.Executor;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.GlueValidation;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

public class ScriptRunnerFormatter implements OutputFormatter {

	private OutputFormatter parent;
	private Script root;
	private List<GlueValidation> errors = new ArrayList<GlueValidation>();

	public ScriptRunnerFormatter(OutputFormatter parent) {
		this.parent = parent;
	}
	
	@Override
	public void start(Script script) {
		if (root == null) {
			root = script;
			synchronized(System.out) {
				System.out.println("Starting: " + script.getName() + " (" + script.getNamespace() + ")");
			}
		}
		parent.start(script);
	}

	@Override
	public void before(Executor executor) {
		parent.before(executor);
	}

	@Override
	public void after(Executor executor) {
		parent.after(executor);
	}

	@Override
	public void validated(GlueValidation...validations) {
		for (GlueValidation validation : validations) {
			if (validation.getSeverity() == Severity.ERROR || validation.getSeverity() == Severity.CRITICAL) {
				errors.add(validation);
			}
		}
		parent.validated(validations);
	}

	@Override
	public void print(Object... messages) {
		parent.print(messages);
	}

	@Override
	public void end(Script script, Date started, Date stopped, Exception exception) {
		if (root != null && root.equals(script)) {
			synchronized(System.out) {
				System.out.print("Finished: " + script.getName() + " (" + script.getNamespace() + ") - ");
				if (stopped == null) {
					if (exception == null) {
						System.out.println("UNKNOWN ERROR");
					}
					else {
						System.out.println("ERROR: " + exception.getMessage());
					}
				}
				else {
					long duration = stopped.getTime() - started.getTime();
					System.out.println(duration + "ms");
				}
				for (GlueValidation error : errors) {
					System.out.println("- " + error);
				}
			}
		}
		parent.end(script, started, stopped, exception);
	}

	public OutputFormatter getParent() {
		return parent;
	}

	@Override
	public boolean shouldExecute(Executor executor) {
		return true;
	}
}
