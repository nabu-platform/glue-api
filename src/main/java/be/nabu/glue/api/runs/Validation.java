package be.nabu.glue.api.runs;

import java.util.Date;
import java.util.List;

import be.nabu.glue.api.Executor;
import be.nabu.glue.api.Script;

public interface Validation {
	
	public enum Level {
		INFO,
		WARN,
		ERROR,
		CRITICAL
	}

	/**
	 * The level of the validation
	 */
	public Level getLevel();
	
	/**
	 * The validation that was performed
	 */
	public String getValidation();
	
	/**
	 * Any message related to the validation
	 */
	public String getMessage();
	
	/**
	 * The call stack at the time of validation
	 */
	public List<Script> getCallStack();
	
	/**
	 * The executor that performed the validation
	 */
	public Executor getExecutor();
	
	/**
	 * When the validation occurred
	 */
	public Date getTimestamp();
}
