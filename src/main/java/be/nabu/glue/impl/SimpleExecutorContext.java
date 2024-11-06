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

import java.util.HashMap;
import java.util.Map;

import be.nabu.glue.api.ExecutorContext;

public class SimpleExecutorContext implements ExecutorContext {

	private int lineNumber, startPosition, endPosition;
	private String label, comment;
	private String line;
	private Map<String, String> annotations = new HashMap<String, String>();
	private String description;
	
	public SimpleExecutorContext(int lineNumber, String label, String comment, String description, String line, Map<String, String> annotations) {
		this.lineNumber = lineNumber;
		this.label = label;
		this.comment = comment;
		this.description = description;
		this.line = line;
		this.annotations.putAll(annotations);
	}

	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@Override
	public Map<String, String> getAnnotations() {
		return annotations;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
}
