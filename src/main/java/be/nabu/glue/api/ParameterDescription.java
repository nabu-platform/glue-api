package be.nabu.glue.api;

import java.util.List;

public interface ParameterDescription {
	public String getName();
	public String getType();
	public String getDescription();
	public Object getDefaultValue();
	public List<?> getEnumerations();
	public boolean isList();
	public boolean isVarargs();
}
