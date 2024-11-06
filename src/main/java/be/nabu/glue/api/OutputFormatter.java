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

package be.nabu.glue.api;

import java.util.Date;

import be.nabu.glue.api.runs.GlueAttachment;
import be.nabu.glue.api.runs.GlueValidation;

public interface OutputFormatter {
	public void start(Script script);
	public void before(Executor executor);
	public void after(Executor executor);
	public void validated(GlueValidation...validations);
	public void print(Object...messages);
	public void end(Script script, Date started, Date stopped, Exception exception);
	public boolean shouldExecute(Executor executor);
	
	public default OutputFormatter getParent() {
		return null;
	}
	public default void attached(GlueAttachment...attachments) {
		// do nothing...
	}
}
