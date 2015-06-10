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
		return getInputs(script.getRoot(), Boolean.parseBoolean(System.getProperty("recursive.inputs", "true")));
	}
	
	public static List<ParameterDescription> getInputs(ExecutorGroup group, boolean recursive) throws ParseException, IOException {
		return getParameters(group, recursive, true);
	}

	public static List<ParameterDescription> getParameters(ExecutorGroup group, boolean recursive, boolean inputOnly) throws ParseException, IOException {
		Map<String, ParameterDescription> parameters = new LinkedHashMap<String, ParameterDescription>();
		for (Executor executor : group.getChildren()) {
			if (executor instanceof AssignmentExecutor) {
				AssignmentExecutor assignmentExecutor = (AssignmentExecutor) executor;
				if (assignmentExecutor.getVariableName() != null && (!assignmentExecutor.isOverwriteIfExists() || !inputOnly)) {
					if (!parameters.containsKey(assignmentExecutor.getVariableName())) {
						Object [] enumerations = null;
						if (assignmentExecutor.getContext().getAnnotations() != null && assignmentExecutor.getContext().getAnnotations().containsKey("enumeration")) {
							enumerations = assignmentExecutor.getContext().getAnnotations().get("enumeration").split("[\\s]*,[\\s]*");
						}
						parameters.put(assignmentExecutor.getVariableName(), new SimpleParameterDescription(assignmentExecutor.getVariableName(), assignmentExecutor.getContext().getComment(), assignmentExecutor.getOptionalType(), false, enumerations == null ? new String[0] : enumerations));
					}
				}
			}
			else if (executor instanceof ExecutorGroup && recursive) {
				for (ParameterDescription childDescription : getInputs((ExecutorGroup) executor, recursive)) {
					if (!parameters.containsKey(childDescription.getName())) {
						parameters.put(childDescription.getName(), childDescription);
					}
				}
			}
		}
		return new ArrayList<ParameterDescription>(parameters.values());
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
					outputs.put(assignmentExecutor.getVariableName(), new SimpleParameterDescription(assignmentExecutor.getVariableName(), assignmentExecutor.getContext().getComment(), assignmentExecutor.getOptionalType()));
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
