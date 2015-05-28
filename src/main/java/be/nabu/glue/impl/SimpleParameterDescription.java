package be.nabu.glue.impl;

import java.util.Arrays;
import java.util.List;

import be.nabu.glue.api.ParameterDescription;

public class SimpleParameterDescription implements ParameterDescription {

	private String name, description, type;
	private boolean isVarargs;
	private List<?> enumerations;
	
	public SimpleParameterDescription(String name, String description, String type) {
		this(name, description, type, false);
	}
	
	public SimpleParameterDescription(String name, String description, String type, boolean isVarargs, Object...enumerations) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.isVarargs = isVarargs;
		this.enumerations = Arrays.asList(enumerations);
	}

	@Override
	public String getName() {
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

	@Override
	public boolean isVarargs() {
		return isVarargs;
	}
}
