package be.nabu.glue.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.ScriptResult;
import be.nabu.glue.api.runs.Validation;
import be.nabu.glue.api.runs.Validation.Level;

public class SimpleScriptResult implements ScriptResult {

	private Script script;
	private Level level;
	private Date started;
	private String log;
	private List<Validation> validations;
	private Date stopped;
	private Exception exception;
	private ExecutionEnvironment environment;

	public SimpleScriptResult(ExecutionEnvironment environment, Script script, Date started, Date stopped, Exception exception, String log, List<Validation> validations) {
		this.environment = environment;
		this.script = script;
		this.started = started;
		this.stopped = stopped;
		this.exception = exception;
		this.log = log;
		this.validations = validations;
		this.level = exception == null ? Level.INFO : Level.ERROR;
		for (Validation validation : validations) {
			switch(validation.getLevel()) {
				case WARN:
					if (Level.INFO.equals(this.level)) {
						this.level = Level.WARN;
					}
				break;
				case ERROR:
					if (Level.INFO.equals(this.level) || Level.WARN.equals(this.level)) {
						this.level = Level.ERROR;
					}
				break;
				case CRITICAL:
					this.level = Level.CRITICAL;
				break;
			}
		}
		try {
			if (Level.ERROR.equals(this.level) && script.getRoot().getContext().getAnnotations().containsKey("critical")) {
				this.level = Level.CRITICAL;
			}
		}
		catch (IOException e) {
			throw new RuntimeException();
		}
		catch (ParseException e) {
			throw new RuntimeException();
		}
		
	}
	
	public SimpleScriptResult(ExecutionEnvironment environment, Script script, Date started, Date stopped, Exception exception, String log, Validation...validations) {
		this(environment, script, started, stopped, exception, log, Arrays.asList(validations));
	}
	
	@Override
	public Level getResultLevel() {
		return level;
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public List<Validation> getValidations() {
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

}
