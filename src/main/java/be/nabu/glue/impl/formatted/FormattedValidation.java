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

package be.nabu.glue.impl.formatted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import be.nabu.glue.api.runs.CallLocation;
import be.nabu.glue.api.runs.GlueValidation;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

@XmlRootElement(name = "validation")
@XmlType(propOrder = { "severity", "timestamp", "group", "validation", "message", "lineNumber", "line", "callStack", "executorId" })
public class FormattedValidation {
	
	private Severity severity;
	private String validation, message, line, group;
	private int lineNumber;
	private List<String> callStack;
	private Date timestamp;
	private String executorId;
	
	public static FormattedValidation format(GlueValidation validation) {
		FormattedValidation formatted = new FormattedValidation();
		formatted.setSeverity(validation.getSeverity());
		formatted.setValidation(validation.getMessage());
		formatted.setMessage(validation.getDescription());
		List<String> callStack = new ArrayList<String>();
		for (CallLocation item : validation.getContext()) {
			callStack.add("[" + item.getScript().getNamespace() + "] " + item.getScript().getName() + (item.getExecutor() != null && item.getExecutor().getContext() != null ? ":" + item.getExecutor().getContext().getLine() : ""));
		}
		formatted.setCallStack(callStack);
		if (validation.getExecutor() != null && validation.getExecutor().getContext() != null) {
			formatted.setGroup(validation.getExecutor().getContext() != null && validation.getExecutor().getContext().getAnnotations() != null ? validation.getExecutor().getContext().getAnnotations().get("group") : null);
			formatted.setLine(validation.getExecutor().getContext().getLine());
			formatted.setLineNumber(validation.getExecutor().getContext().getLineNumber());
			formatted.setExecutorId(validation.getExecutor().getContext() != null && validation.getExecutor().getContext().getAnnotations() != null ? validation.getExecutor().getContext().getAnnotations().get("id") : null);
		}
		formatted.setTimestamp(validation.getCreated());
		return formatted;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public List<String> getCallStack() {
		return callStack;
	}

	public void setCallStack(List<String> callStack) {
		this.callStack = callStack;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getExecutorId() {
		return executorId;
	}

	public void setExecutorId(String executorId) {
		this.executorId = executorId;
	}
	
}
