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

import java.util.List;

import be.nabu.glue.api.MethodDescription;
import be.nabu.glue.api.ParameterDescription;

public class SimpleMethodDescription implements MethodDescription {

	private String description;
	private String name, namespace;
	private List<ParameterDescription> parameters;
	private List<ParameterDescription> returnValues;
	private boolean isNamedParametersAllowed;
	private Double version;

	public SimpleMethodDescription(String namespace, String name, String description, List<ParameterDescription> parameters, List<ParameterDescription> returnValues) {
		this(namespace, name, description, parameters, returnValues, false);
	}
	
	public SimpleMethodDescription(String namespace, String name, String description, List<ParameterDescription> parameters, List<ParameterDescription> returnValues, boolean isNamedParametersAllowed) {
		this(namespace, name, description, parameters, returnValues, isNamedParametersAllowed, null);
	}
	
	public SimpleMethodDescription(String namespace, String name, String description, List<ParameterDescription> parameters, List<ParameterDescription> returnValues, boolean isNamedParametersAllowed, Double version) {
		this.namespace = namespace;
		this.name = name;
		this.description = description;
		this.parameters = parameters;
		this.returnValues = returnValues;
		this.isNamedParametersAllowed = isNamedParametersAllowed;
		this.version = version;
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
	
	@Override
	public String toString() {
		String content = namespace == null ? "" : namespace + ".";
		content += name;
		return description == null ? content : content + ": " + description;
	}

	@Override
	public Double getVersion() {
		return version;
	}
}
