package be.nabu.glue.impl;

import java.util.Arrays;
import java.util.List;

import be.nabu.glue.api.ParameterDescription;

public class SimpleParameterDescription implements ParameterDescription {

	private String name, description, type;
	private Object defaultValue;
	private boolean isVarargs, isList;
	private List<?> enumerations;
	
	public SimpleParameterDescription() {}
	
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
	
	@Override
	public boolean isList() {
		return isList;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	public SimpleParameterDescription setName(String name) {
		this.name = name;
		return this;
	}

	public SimpleParameterDescription setDescription(String description) {
		this.description = description;
		return this;
	}

	public SimpleParameterDescription setType(String type) {
		this.type = type;
		return this;
	}

	public SimpleParameterDescription setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public SimpleParameterDescription setVarargs(boolean isVarargs) {
		this.isVarargs = isVarargs;
		return this;
	}

	public SimpleParameterDescription setEnumerations(List<?> enumerations) {
		this.enumerations = enumerations;
		return this;
	}
	
	public SimpleParameterDescription setEnumerations(Object...enumerations) {
		this.enumerations = Arrays.asList(enumerations);
		return this;
	}
	
	public SimpleParameterDescription setList(boolean isList) {
		this.isList = isList;
		return this;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
