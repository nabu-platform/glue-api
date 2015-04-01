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
