/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.glue.impl;

import java.util.ArrayList;
import java.util.List;
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
		this.pattern = compile(identifier);
	}
	
	private static Pattern compile(String identifier) {
		return Pattern.compile("(?<!\\\\)(" + Pattern.quote(identifier) + "\\{)");
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
			if (query == null) {
				throw new IllegalArgumentException("The opening " + identifier + "{ is missing an end tag");
			}
			// quote the string markers in the replacement
			target = target.replaceAll(Pattern.quote(matcher.group() + query + "}"), "\\${" + Matcher.quoteReplacement(methodSignature.replace("${value}", query.replace("'", "\'").replace("\"", "\\\""))) + "}");
		}
		return target;
	}
	
	public static List<String> getValues(String identifier, String value) {
		List<String> values = new ArrayList<String>();
		Matcher matcher = compile(identifier).matcher(value);
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
			if (query == null) {
				throw new IllegalArgumentException("The opening " + identifier + "{ is missing an end tag");
			}
			values.add(query);
		}
		return values;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public String getMethodSignature() {
		return methodSignature;
	}
}
