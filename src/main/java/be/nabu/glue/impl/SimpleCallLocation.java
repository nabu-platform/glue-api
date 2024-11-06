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

import be.nabu.glue.api.Executor;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.CallLocation;

public class SimpleCallLocation implements CallLocation {

	private Script script;
	private Executor executor;
	
	public SimpleCallLocation(Script script, Executor executor) {
		this.script = script;
		this.executor = executor;
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}
	
	@Override
	public String toString() {
		String message = script.getName();
		if (executor != null && executor.getContext() != null) {
			message += ":" + (executor.getContext().getLineNumber() + 1);
		}
		return message;
	}

}
