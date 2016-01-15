package be.nabu.glue.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.StringSubstituter;

public class ImperativeSubstitutor implements StringSubstituter {

	private String identifier;
	private String methodSignature;
	private Pattern pattern;

	public ImperativeSubstitutor(String identifier, String methodSignature) {
		if (identifier.endsWith("$")) {
			throw new IllegalArgumentException("The $ sign is reserved for inline glue");
		}
		this.identifier = identifier;
		this.methodSignature = methodSignature;
		this.pattern = Pattern.compile("(?<!\\\\)" + Pattern.quote(identifier) + "\\{([^}]+)\\}");
	}
	
	@Override
	public String substitute(String value, ExecutionContext context, boolean allowNull) {
		Matcher matcher = pattern.matcher(value);
		while (matcher.find()) {
			String query = matcher.group().replaceAll(pattern.pattern(), "$1");
			value = value.replaceAll(Pattern.quote(matcher.group()), "\\${" + Matcher.quoteReplacement(methodSignature.replace("${value}", query)) + "}");
		}
		return value;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getMethodSignature() {
		return methodSignature;
	}
}
