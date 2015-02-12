package be.nabu.glue.api.runs;

import java.util.Date;
import java.util.List;

import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.Validation.Level;

public interface ScriptResult {
	/**
	 * The level of the complete test case
	 */
	public Level getResultLevel();
	
	/**
	 * The script being tested
	 */
	public Script getScript();
	
	/**
	 * The validations that occurred during the script run
	 */
	public List<Validation> getValidations();
	
	/**
	 * When the script started execution
	 */
	public Date getStarted();
	
	/**
	 * When it ended execution
	 */
	public Date getStopped();
	
	/**
	 * If the script ended with an exception, this is it
	 */
	public Exception getException();
	
	/**
	 * The log generated during the run
	 */
	public String getLog();
	
	/**
	 * The environment it was executed on
	 * @return
	 */
	public ExecutionEnvironment getEnvironment();
}
