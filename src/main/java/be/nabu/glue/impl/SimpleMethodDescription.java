package be.nabu.glue.impl;

import java.util.List;

import be.nabu.glue.api.MethodDescription;
import be.nabu.glue.api.ParameterDescription;

public class SimpleMethodDescription implements MethodDescription {

	private String description;
	private String name;
	private List<ParameterDescription> parameters;
	private List<ParameterDescription> returnValues;

	public SimpleMethodDescription(String name, String description, List<ParameterDescription> parameters, List<ParameterDescription> returnValues) {
		this.name = name;
		this.description = description;
		this.parameters = parameters;
		this.returnValues = returnValues;
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
}
