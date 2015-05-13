package be.nabu.glue.impl.formatted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import be.nabu.glue.api.runs.CallLocation;
import be.nabu.glue.api.runs.Validation;
import be.nabu.glue.api.runs.Validation.Level;

@XmlRootElement(name = "validation")
@XmlType(propOrder = { "level", "timestamp", "group", "validation", "message", "lineNumber", "line", "callStack" })
public class FormattedValidation {
	
	private Level level;
	private String validation, message, line, group;
	private int lineNumber;
	private List<String> callStack;
	private Date timestamp;
	
	public static FormattedValidation format(Validation validation) {
		FormattedValidation formatted = new FormattedValidation();
		formatted.setLevel(validation.getLevel());
		formatted.setValidation(validation.getValidation());
		formatted.setMessage(validation.getMessage());
		List<String> callStack = new ArrayList<String>();
		for (CallLocation item : validation.getCallStack()) {
			callStack.add("[" + item.getScript().getNamespace() + "] " + item.getScript().getName() + (item.getExecutor().getContext() != null ? ":" + item.getExecutor().getContext().getLine() : ""));
		}
		formatted.setCallStack(callStack);
		if (validation.getExecutor() != null) {
			formatted.setGroup(validation.getExecutor().getContext() != null && validation.getExecutor().getContext().getAnnotations() != null ? validation.getExecutor().getContext().getAnnotations().get("group") : null);
			formatted.setLine(validation.getExecutor().getContext().getLine());
			formatted.setLineNumber(validation.getExecutor().getContext().getLineNumber());
		}
		formatted.setTimestamp(validation.getTimestamp());
		return formatted;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public List<String> getCallStack() {
		return callStack;
	}

	public void setCallStack(List<String> callStack) {
		this.callStack = callStack;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
