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
}
