package be.nabu.glue.impl;

import java.util.Arrays;
import java.util.List;

import be.nabu.glue.api.MethodDescription;
import be.nabu.glue.api.ParameterDescription;

public class SimpleMethodDescription implements MethodDescription {

	private String description;
	private String name;
	private List<ParameterDescription> parameters;

	public SimpleMethodDescription(String name, String description, ParameterDescription...parameters) {
		this.name = name;
		this.description = description;
		this.parameters = Arrays.asList(parameters);
		
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

}
