/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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
		// it can be a list without being varargs, but it can not be varargs without being a list
		this.isList = isVarargs;
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
