package be.nabu.glue.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.nabu.glue.api.AssignmentExecutor;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.MethodDescription;
import be.nabu.glue.api.ParameterDescription;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptRepository;
import be.nabu.glue.impl.SimpleMethodDescription;
import be.nabu.glue.impl.SimpleParameterDescription;

public class ScriptUtils {
	
	private static Date buildTime;
	
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

	public static List<MethodDescription> buildDescriptionsFor(ScriptRepository repository) {
		List<MethodDescription> descriptions = new ArrayList<MethodDescription>();
		for (Script script : repository) {
			try {
				descriptions.add(new SimpleMethodDescription(script.getNamespace(), script.getName(), script.getRoot().getContext().getComment(), ScriptUtils.getInputs(script), ScriptUtils.getOutputs(script)));
			}
			catch (IOException e) {
				// ignore
			}
			catch (ParseException e) {
				// ignore
			}
			catch (RuntimeException e) {
				// ignore
			}
		}
		return descriptions;
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
			// we skip assignment executors even if they are groups, this is currently _only_ the case for lambdas which we don't want to inspect
			// in the future this may need an extension of the interface to mark it for inspection
			else if (executor instanceof ExecutorGroup && recursive && !(executor instanceof AssignmentExecutor)) {
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
		return getOutputs(script, null);
	}
	
	public static List<ParameterDescription> getOutputs(Script script, ExecutorFilter filter) throws ParseException, IOException {
		return new ArrayList<ParameterDescription>(getOutputs(script.getRoot(), filter).values());
	}
	
	private static Map<String, ParameterDescription> getOutputs(ExecutorGroup group, ExecutorFilter filter) {
		Map<String, ParameterDescription> outputs = new LinkedHashMap<String, ParameterDescription>();
		for (Executor executor : group.getChildren()) {
			if (executor instanceof AssignmentExecutor && (filter == null || filter.accept(executor))) {
				AssignmentExecutor assignmentExecutor = (AssignmentExecutor) executor;
				if (assignmentExecutor.getVariableName() != null) {
					outputs.put(assignmentExecutor.getVariableName(), new SimpleParameterDescription(assignmentExecutor.getVariableName(), assignmentExecutor.getContext().getComment(), assignmentExecutor.getOptionalType())
						.setList(assignmentExecutor.isList()));
				}
			}
			// see logic description of assignmentexecutor check in getparameters
			if (executor instanceof ExecutorGroup && !(executor instanceof AssignmentExecutor)) {
				outputs.putAll(getOutputs((ExecutorGroup) executor, filter));
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
	
	public static Date getBuildTime() {
		if (buildTime == null) {
			synchronized(ScriptUtils.class) {
				if (buildTime == null) {
					try {
						Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
						while (resources.hasMoreElements()) {
							URL url = resources.nextElement();
							if (url.getPath().matches(".*(^|/)glue-api-[^/!]+\\.jar.*")) {
								InputStream input = new BufferedInputStream(url.openStream());
								try {
									int read = 0;
									ByteArrayOutputStream output = new ByteArrayOutputStream();
									byte [] buffer = new byte[4096];
									while ((read = input.read(buffer)) > 0) {
										output.write(buffer, 0, read);
									}
									String content = new String(output.toByteArray(), "UTF-8");
									for (String line : content.split("[\r\n]+")) {
										String [] parts = line.trim().split(":");
										if (parts.length == 2 && "Build-Time".equalsIgnoreCase(parts[0].trim())) {
											SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmm");
											buildTime = formatter.parse(parts[1].trim());
											break;
										}
									}
								}
								finally {
									input.close();
								}
							}
							if (buildTime == null) {
								buildTime = new Date(0);
							}
						}
					}
					catch (ParseException e) {
						buildTime = new Date(0);
					}
					catch (IOException e) {
						buildTime = new Date(0);
					}
				}
			}
		}
		return buildTime;
	}
	
	public static interface ExecutorFilter {
		public boolean accept(Executor executor);
	}
}
