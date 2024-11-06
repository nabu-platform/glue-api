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

package be.nabu.glue.utils;

import be.nabu.glue.api.Executor;

public class ScriptRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = -4284853327836193254L;
	private ScriptRuntime runtime;
	private String message;

	public ScriptRuntimeException(ScriptRuntime runtime, Throwable cause) {
		super(cause);
		this.runtime = runtime;
	}
	
	public ScriptRuntimeException(ScriptRuntime runtime, String message) {
		super(message);
		this.runtime = runtime;
		this.message = message;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		if (runtime == null || runtime.getScript() == null) {
			builder.append("Unknown error");
		}
		else {
			builder.append("Error occurred in " + runtime.getScript().getName());
		}
		if (runtime.getExecutionContext().getCurrent() != null) {
			Executor executor = runtime.getExecutionContext().getCurrent();
			builder.append(" at line " + (executor.getContext().getLineNumber() + 1) + ": " + executor.getContext().getLine());
		}
		if (message != null) {
			builder.append(": " + message);
		}
		return builder.toString();
	}
	
}
