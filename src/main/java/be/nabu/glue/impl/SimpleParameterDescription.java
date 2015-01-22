package be.nabu.glue.impl;

import java.util.Arrays;
import java.util.List;

import be.nabu.glue.api.ParameterDescription;

public class SimpleParameterDescription implements ParameterDescription {

	private String name, description, type;
	
	private List<?> enumerations;
	
	public SimpleParameterDescription(String name, String description, String type, Object...enumerations) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.enumerations = Arrays.asList(enumerations);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<?> getEnumerations() {
		return enumerations;
	}
}
