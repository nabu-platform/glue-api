package be.nabu.glue.impl;

import be.nabu.glue.api.ParameterDescription;

public class SimpleParameterDescription implements ParameterDescription {

	private String name, description, type;
	
	public SimpleParameterDescription(String name, String description, String type) {
		this.name = name;
		this.description = description;
		this.type = type;
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
}
