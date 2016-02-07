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
		this.pattern = Pattern.compile("(?<!\\\\)(" + Pattern.quote(identifier) + "\\{)");
	}
	
	@Override
	public String substitute(String value, ExecutionContext context, boolean allowNull) {
		Matcher matcher = pattern.matcher(value);
		String target = value;
		while (matcher.find()) {
			int depth = 0;
			String query = null;
			// we start after the capturing group
			int contentStart = matcher.start() + matcher.group(1).length();
			for (int i = contentStart; i < value.length(); i++) {
				if (value.charAt(i) == '{') {
					depth++;
				}
				else if (value.charAt(i) == '}') {
					if (depth == 0) {
						query = value.substring(contentStart, i);
						break;
					}
					else {
						depth--;
					}
				}
			}
			target = target.replaceAll(Pattern.quote(matcher.group() + query + "}"), "\\${" + Matcher.quoteReplacement(methodSignature.replace("${value}", query)) + "}");
		}
		return target;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getMethodSignature() {
		return methodSignature;
	}
}
