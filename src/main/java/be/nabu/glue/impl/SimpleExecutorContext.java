package be.nabu.glue.impl;

import java.util.HashMap;
import java.util.Map;

import be.nabu.glue.api.ExecutorContext;

public class SimpleExecutorContext implements ExecutorContext {

	private int lineNumber;
	private String label, comment;
	private String line;
	private Map<String, String> annotations = new HashMap<String, String>();
	
	public SimpleExecutorContext(int lineNumber, String label, String comment, String line, Map<String, String> annotations) {
		this.lineNumber = lineNumber;
		this.label = label;
		this.comment = comment;
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
	
	@Override
	public Map<String, String> getAnnotations() {
		return annotations;
	}
}
