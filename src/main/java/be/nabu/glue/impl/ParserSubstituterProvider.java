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

import be.nabu.glue.api.StringSubstituter;
import be.nabu.glue.api.StringSubstituterProvider;
import be.nabu.glue.utils.ScriptRuntime;

public class ParserSubstituterProvider implements StringSubstituterProvider {
	@Override
	public StringSubstituter getSubstituter(ScriptRuntime runtime) {
		return runtime.getScript().getParser();
	}
}
