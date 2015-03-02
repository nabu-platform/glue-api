package be.nabu.glue.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.nabu.glue.api.Executor;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.Validation;
import be.nabu.glue.api.runs.Validation.Level;

/**
 * IMPORTANT: this formatter does not support multithreaded execution, this is a known current limitation 
 */
public class ScriptRunnerFormatter implements OutputFormatter {

	private OutputFormatter parent;
	private Script root;
	private List<Validation> errors = new ArrayList<Validation>();

	public ScriptRunnerFormatter(OutputFormatter parent) {
		this.parent = parent;
	}
	
	@Override
	public void start(Script script) {
		if (root == null) {
			root = script;
			System.out.print("Running: " + script.getName() + " (" + script.getNamespace() + ")...");
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
	public void validated(Validation...validations) {
		for (Validation validation : validations) {
			if (validation.getLevel() == Level.ERROR || validation.getLevel() == Level.CRITICAL) {
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
			for (Validation error : errors) {
				System.out.println("- " + error);
			}
		}
		parent.end(script, started, stopped, exception);
	}

	public OutputFormatter getParent() {
		return parent;
	}
}
