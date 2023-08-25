package be.nabu.glue.api.runs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.Script;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

public interface ScriptResult {
	/**
	 * The level of the complete test case
	 */
	public Severity getResultLevel();
	
	/**
	 * The script being tested
	 */
	public Script getScript();
	
	/**
	 * The validations that occurred during the script run
	 */
	public List<GlueValidation> getValidations();
	
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
	
	/**
	 * The attachments that were generated during the script run
	 */
	public default List<GlueAttachment> getAttachments() {
		return new ArrayList<GlueAttachment>();
	}
}
