package be.nabu.glue.api;

import java.util.Map;

public interface ExecutorContext {
	public int getLineNumber();
	public String getLabel();
	public String getComment();
	public String getDescription();
	public String getLine();
	public Map<String, String> getAnnotations();
	public int getStartPosition();
	public int getEndPosition();
}
