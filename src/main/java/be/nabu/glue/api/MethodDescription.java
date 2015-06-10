package be.nabu.glue.api;

import java.util.List;

public interface MethodDescription {
	public String getName();
	public String getNamespace();
	public String getDescription();
	public List<ParameterDescription> getParameters();
	public List<ParameterDescription> getReturnValues();
	public boolean isNamedParametersAllowed();
}
