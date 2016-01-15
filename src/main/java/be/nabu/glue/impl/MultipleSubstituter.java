package be.nabu.glue.impl;

import java.util.Collection;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.StringSubstituter;

public class MultipleSubstituter implements StringSubstituter {

	private Collection<StringSubstituter> substituters;

	public MultipleSubstituter(Collection<StringSubstituter> substituters) {
		this.substituters = substituters;
	}
	
	@Override
	public String substitute(String value, ExecutionContext context, boolean allowNull) {
		for (StringSubstituter substituter : substituters) {
			value = substituter.substitute(value, context, allowNull);
		}
		return value;
	}

}
