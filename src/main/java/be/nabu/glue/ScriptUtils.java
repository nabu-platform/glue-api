package be.nabu.glue;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.nabu.glue.api.AssignmentExecutor;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.ParameterDescription;
import be.nabu.glue.api.Script;
import be.nabu.glue.impl.SimpleParameterDescription;

public class ScriptUtils {
	
	public static List<ParameterDescription> getInputs(Script script) throws ParseException, IOException {
		Map<String, ParameterDescription> inputs = new LinkedHashMap<String, ParameterDescription>();
		for (Executor executor : script.getRoot().getChildren()) {
			if (executor instanceof AssignmentExecutor) {
				AssignmentExecutor assignmentExecutor = (AssignmentExecutor) executor;
				if (assignmentExecutor.getVariableName() != null && !assignmentExecutor.isOverwriteIfExists()) {
					if (!inputs.containsKey(assignmentExecutor.getVariableName())) {
						Object [] enumerations = null;
						if (assignmentExecutor.getContext().getAnnotations() != null && assignmentExecutor.getContext().getAnnotations().containsKey("enumeration")) {
							enumerations = assignmentExecutor.getContext().getAnnotations().get("enumeration").split("[\\s]*,[\\s]*");
						}
						inputs.put(assignmentExecutor.getVariableName(), new SimpleParameterDescription(assignmentExecutor.getVariableName(), assignmentExecutor.getContext().getComment(), null, enumerations == null ? new String[0] : enumerations));
					}
				}
			}
		}
		return new ArrayList<ParameterDescription>(inputs.values());
	}
	
	public static List<ParameterDescription> getOutputs(Script script) throws ParseException, IOException {
		return new ArrayList<ParameterDescription>(getOutputs(script.getRoot()).values());
	}
	
	private static Map<String, ParameterDescription> getOutputs(ExecutorGroup group) {
		Map<String, ParameterDescription> outputs = new LinkedHashMap<String, ParameterDescription>();
		for (Executor executor : group.getChildren()) {
			if (executor instanceof AssignmentExecutor) {
				AssignmentExecutor assignmentExecutor = (AssignmentExecutor) executor;
				if (assignmentExecutor.getVariableName() != null) {
					outputs.put(assignmentExecutor.getVariableName(), new SimpleParameterDescription(assignmentExecutor.getVariableName(), assignmentExecutor.getContext().getComment(), null));
				}
			}
			if (executor instanceof ExecutorGroup) {
				outputs.putAll(getOutputs((ExecutorGroup) executor));
			}
		}
		return outputs;
	}
	
	public static String getFullName(Script script) {
		String name = script.getName();
		if (script.getNamespace() != null) {
			name = script.getNamespace() + "." + name;
		}
		return name;
	}
}
