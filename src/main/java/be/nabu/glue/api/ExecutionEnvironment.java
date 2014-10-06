package be.nabu.glue.api;

import java.util.Map;

public interface ExecutionEnvironment {
	public String getName();
	public Map<String, String> getParameters();
}