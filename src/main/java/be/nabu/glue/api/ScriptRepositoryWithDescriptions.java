package be.nabu.glue.api;

import java.util.Collection;

public interface ScriptRepositoryWithDescriptions extends ScriptRepository {
	public Collection<MethodDescription> getDescriptions();
}
