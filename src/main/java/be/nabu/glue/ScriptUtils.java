package be.nabu.glue;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import be.nabu.glue.api.AssignmentExecutor;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.ParameterDescription;
import be.nabu.glue.api.Script;
import be.nabu.glue.impl.SimpleParameterDescription;

public class ScriptUtils {
	public static List<ParameterDescription> getInputs(Script script) throws ParseException, IOException {
		List<ParameterDescription> inputs = new ArrayList<ParameterDescription>();
		for (Executor executor : script.getRoot().getChildren()) {
			if (executor instanceof AssignmentExecutor) {
				AssignmentExecutor assignmentExecutor = (AssignmentExecutor) executor;
				if (assignmentExecutor.getVariableName() != null && !assignmentExecutor.isOverwriteIfExists()) {
					if (!inputs.contains(assignmentExecutor.getVariableName())) {
						inputs.add(new SimpleParameterDescription(assignmentExecutor.getVariableName(), assignmentExecutor.getContext().getComment(), null));
					}
				}
			}
		}
		return inputs;
	}
	
	public static String getFullName(Script script) {
		String name = script.getName();
		if (script.getNamespace() != null) {
			name = script.getNamespace() + "." + name;
		}
		return name;
	}
}
