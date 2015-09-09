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
import be.nabu.glue.api.ScriptRepository;
import be.nabu.glue.impl.SimpleParameterDescription;

public class ScriptUtils {
	
	public static ScriptRepository getRoot(ScriptRepository repository) {
		while (repository.getParent() != null) {
			repository = repository.getParent();
		}
		return repository;
	}
	
	public static Executor getExecutor(ExecutorGroup group, String id) {
		for (Executor child : group.getChildren()) {
			if (id.equals(child.getId())) {
				return child;
			}
			if (child instanceof ExecutorGroup) {
				Executor executor = getExecutor((ExecutorGroup) child, id);
				if (executor != null) {
					return executor;
				}
			}
		}
		return null;
	}
	
	public static Executor getLine(ExecutorGroup group, int lineNumber) {
		for (Executor child : group.getChildren()) {
			if (child.getContext() != null && child.getContext().getLineNumber() == lineNumber) {
				return child;
			}
			if (child instanceof ExecutorGroup) {
				Executor line = getLine((ExecutorGroup) child, lineNumber);
				if (line != null) {
					return line;
				}
			}
		}
		return null;
	}
	
	public static List<ParameterDescription> getInputs(Script script) throws ParseException, IOException {
		List<ParameterDescription> inputs = getInputs(script.getRoot(), Boolean.parseBoolean(System.getProperty("recursive.inputs", "true")));
		// if the last of the inputs is an explicit array, set the varargs boolean
		if (!inputs.isEmpty()) {
			if (inputs.get(inputs.size() - 1).isList()) {
				((SimpleParameterDescription) inputs.get(inputs.size() - 1)).setVarargs(true);
			}
		}
		return inputs;
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
						parameters.put(assignmentExecutor.getVariableName(), new SimpleParameterDescription(assignmentExecutor.getVariableName(), assignmentExecutor.getContext().getComment(), assignmentExecutor.getOptionalType(), false, enumerations == null ? new String[0] : enumerations)
								.setList(assignmentExecutor.isList()));
					}
				}
			}
			else if (executor instanceof ExecutorGroup && recursive) {
				for (ParameterDescription childDescription : getParameters((ExecutorGroup) executor, recursive, inputOnly)) {
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
					outputs.put(assignmentExecutor.getVariableName(), new SimpleParameterDescription(assignmentExecutor.getVariableName(), assignmentExecutor.getContext().getComment(), assignmentExecutor.getOptionalType())
						.setList(assignmentExecutor.isList()));
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
