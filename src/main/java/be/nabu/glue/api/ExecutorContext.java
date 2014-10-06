package be.nabu.glue.api;

import java.util.Map;

public interface ExecutorContext {
	public int getLineNumber();
	public String getLabel();
	public String getComment();
	public String getLine();
	public Map<String, String> getAnnotations();
}
