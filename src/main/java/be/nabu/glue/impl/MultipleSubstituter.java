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

import java.util.Collection;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.StringSubstituter;

public class MultipleSubstituter implements StringSubstituter {

	private Collection<StringSubstituter> substituters;

	public MultipleSubstituter(Collection<StringSubstituter> substituters) {
		this.substituters = substituters;
	}
	
	@Override
	public String substitute(String value, ExecutionContext context, boolean allowNull) {
		for (StringSubstituter substituter : substituters) {
			value = substituter.substitute(value, context, allowNull);
		}
		return value;
	}

}
