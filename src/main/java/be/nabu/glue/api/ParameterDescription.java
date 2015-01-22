package be.nabu.glue.api;

import java.util.List;

public interface ParameterDescription {
	public String getName();
	public String getType();
	public String getDescription();
	public List<?> getEnumerations();
}
