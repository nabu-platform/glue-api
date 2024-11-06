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
