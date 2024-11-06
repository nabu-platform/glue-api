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

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.ScriptResult;
import be.nabu.glue.api.runs.GlueAttachment;
import be.nabu.glue.api.runs.GlueValidation;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

public class SimpleScriptResult implements ScriptResult {

	private Script script;
	private Severity severity;
	private Date started;
	private String log;
	private List<GlueValidation> validations;
	private List<GlueAttachment> attachments;
	private Date stopped;
	private Exception exception;
	private ExecutionEnvironment environment;

	public SimpleScriptResult(ExecutionEnvironment environment, Script script, Date started, Date stopped, Exception exception, String log, List<GlueValidation> validations, List<GlueAttachment> attachments) {
		this.environment = environment;
		this.script = script;
		this.started = started;
		this.stopped = stopped;
		this.exception = exception;
		this.log = log;
		this.validations = validations;
		this.attachments = attachments;
		this.severity = exception == null ? Severity.INFO : Severity.ERROR;
		for (GlueValidation validation : validations) {
			switch(validation.getSeverity()) {
				case WARNING:
					if (Severity.INFO.equals(this.severity)) {
						this.severity = Severity.WARNING;
					}
				break;
				case ERROR:
					if (Severity.INFO.equals(this.severity) || Severity.WARNING.equals(this.severity)) {
						this.severity = Severity.ERROR;
					}
				break;
				case CRITICAL:
					this.severity = Severity.CRITICAL;
				break;
			}
		}
		try {
			if (Severity.ERROR.equals(this.severity) && script.getRoot().getContext().getAnnotations().containsKey("critical")) {
				this.severity = Severity.CRITICAL;
			}
		}
		catch (IOException e) {
			throw new RuntimeException();
		}
		catch (ParseException e) {
			throw new RuntimeException();
		}
		
	}
	
	public SimpleScriptResult(ExecutionEnvironment environment, Script script, Date started, Date stopped, Exception exception, String log, GlueValidation...validations) {
		this(environment, script, started, stopped, exception, log, Arrays.asList(validations), null);
	}
	
	@Override
	public Severity getResultLevel() {
		return severity;
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public List<GlueValidation> getValidations() {
		return validations;
	}

	@Override
	public Date getStarted() {
		return started;
	}

	@Override
	public Date getStopped() {
		return stopped;
	}

	@Override
	public Exception getException() {
		return exception;
	}

	@Override
	public String getLog() {
		return log;
	}

	@Override
	public ExecutionEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public List<GlueAttachment> getAttachments() {
		return attachments;
	}

}
