package be.nabu.glue.impl;

import java.util.List;

import be.nabu.glue.api.MethodDescription;
import be.nabu.glue.api.ParameterDescription;

public class SimpleMethodDescription implements MethodDescription {

	private String description;
	private String name, namespace;
	private List<ParameterDescription> parameters;
	private List<ParameterDescription> returnValues;
	private boolean isNamedParametersAllowed;

	public SimpleMethodDescription(String namespace, String name, String description, List<ParameterDescription> parameters, List<ParameterDescription> returnValues) {
		this(namespace, name, description, parameters, returnValues, false);
	}
	
	public SimpleMethodDescription(String namespace, String name, String description, List<ParameterDescription> parameters, List<ParameterDescription> returnValues, boolean isNamedParametersAllowed) {
		this.namespace = namespace;
		this.name = name;
		this.description = description;
		this.parameters = parameters;
		this.returnValues = returnValues;
		this.isNamedParametersAllowed = isNamedParametersAllowed;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<ParameterDescription> getParameters() {
		return parameters;
	}

	@Override
	public List<ParameterDescription> getReturnValues() {
		return returnValues;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public boolean isNamedParametersAllowed() {
		return isNamedParametersAllowed;
	}
	public void setNamedParametersAllowed(boolean isNamedParametersAllowed) {
		this.isNamedParametersAllowed = isNamedParametersAllowed;
	}
}
