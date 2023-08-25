package be.nabu.glue.impl.formatted;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import be.nabu.glue.api.runs.GlueAttachment;

@XmlType(propOrder = { "timestamp", "group", "executorId", "name", "contentType", "content", "message", "group", "line", "lineNumber" })
public class FormattedAttachment {
	private String name, contentType, executorId, message, group, line;
	private byte[] content;
	private Date timestamp;
	private int lineNumber;
//	private List<String> callStack;
	
	public static FormattedAttachment format(GlueAttachment attachment) {
		FormattedAttachment formatted = new FormattedAttachment();
		formatted.setMessage(attachment.getMessage());
		formatted.setContent(attachment.getContent());
		formatted.setContentType(attachment.getContentType());
		formatted.setName(attachment.getName());
		formatted.setTimestamp(attachment.getCreated());
//		List<String> callStack = new ArrayList<String>();
//		for (CallLocation item : attachment.getContext()) {
//			callStack.add("[" + item.getScript().getNamespace() + "] " + item.getScript().getName() + (item.getExecutor() != null && item.getExecutor().getContext() != null ? ":" + item.getExecutor().getContext().getLine() : ""));
//		}
//		formatted.setCallStack(callStack);
		if (attachment.getExecutor() != null && attachment.getExecutor().getContext() != null) {
			formatted.setGroup(attachment.getExecutor().getContext() != null && attachment.getExecutor().getContext().getAnnotations() != null ? attachment.getExecutor().getContext().getAnnotations().get("group") : null);
			formatted.setLine(attachment.getExecutor().getContext().getLine());
			formatted.setLineNumber(attachment.getExecutor().getContext().getLineNumber());
			formatted.setExecutorId(attachment.getExecutor().getContext() != null && attachment.getExecutor().getContext().getAnnotations() != null ? attachment.getExecutor().getContext().getAnnotations().get("id") : null);
		}
		return formatted;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getExecutorId() {
		return executorId;
	}
	public void setExecutorId(String executorId) {
		this.executorId = executorId;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}
