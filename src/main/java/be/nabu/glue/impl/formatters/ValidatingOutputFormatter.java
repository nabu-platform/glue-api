package be.nabu.glue.impl.formatters;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import be.nabu.glue.ScriptRuntime;
import be.nabu.glue.api.AssignmentExecutor;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.OutputFormatter;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.runs.GlueValidation;
import be.nabu.libs.converter.ConverterFactory;

public class ValidatingOutputFormatter implements OutputFormatter {

	private OutputFormatter parent;
	
	private static List<String> languages = Arrays.asList(Locale.getISOLanguages());
	private static List<String> countries = Arrays.asList(Locale.getISOCountries());
	private static List<String> timezones = Arrays.asList(TimeZone.getAvailableIDs());
	private static List<String> charsets = new ArrayList<String>(Charset.availableCharsets().keySet());

	public ValidatingOutputFormatter(OutputFormatter parent) {
		this.parent = parent;
	}
	
	@Override
	public void start(Script script) {
		parent.start(script);
	}

	@Override
	public void after(Executor executor) {
		// first trigger parent, it might perform some operation that influences the value
		parent.after(executor);
		// then validate the result
		if (executor instanceof AssignmentExecutor && ((AssignmentExecutor) executor).getVariableName() != null && executor.getContext() != null && executor.getContext().getAnnotations() != null) {
			String name = ((AssignmentExecutor) executor).getVariableName();
			if (name != null) {
				Object value = ScriptRuntime.getRuntime().getExecutionContext().getPipeline().get(name);
				Map<String, String> annotations = executor.getContext().getAnnotations();
				if (annotations.containsKey("pattern")) {
					if (value != null) {
						pattern(value, annotations.get("pattern"));
					}
				}
				if (annotations.containsKey("enumeration")) {
					if (value != null) {
						enumeration(value, annotations.get("enumeration"));
					}
				}
				if (annotations.containsKey("null")) {
					nullable(value, annotations.get("null"));
				}
				if (annotations.containsKey("max")) {
					if (value != null) {
						max(value, annotations.get("max"));
					}
				}
				if (annotations.containsKey("min")) {
					if (value != null) {
						min(value, annotations.get("min"));
					}
				}
			}
		}
	}
	
	private void nullable(Object value, String nullable) {
		if (nullable == null || nullable.trim().isEmpty()) {
			nullable = "true";
		}
		boolean isNullable = Boolean.parseBoolean(nullable);
		if (value == null && !isNullable) {
			throw new IllegalArgumentException("Null is not allowed");
		}
		else if (value != null && isNullable) {
			throw new IllegalArgumentException("The value must be null");
		}
	}
	
	private void max(Object value, String max) {
		if (max == null || max.trim().isEmpty()) {
			throw new IllegalStateException("The max value is empty");
		}
		if (value instanceof Number) {
			if (max.contains(".")) {
				if (((Number) value).doubleValue() > Double.parseDouble(max)) {
					throw new IllegalArgumentException("The value '" + value + "' is larger than the allowed '" + max + "'");
				}
			}
			else {
				if (((Number) value).longValue() > Long.parseLong(max)) {
					throw new IllegalArgumentException("The value '" + value + "' is larger than the allowed '" + max + "'");
				}
			}
		}
		else {
			String string = toString(value);
			if (string.length() > Integer.parseInt(max)) {
				throw new IllegalArgumentException("The value '" + value + "' has more characters than allowed: " + max);
			}
		}
	}

	private void min(Object value, String min) {
		if (min == null || min.trim().isEmpty()) {
			throw new IllegalStateException("The min value is empty");
		}
		if (value instanceof Number) {
			if (min.contains(".")) {
				if (((Number) value).doubleValue() < Double.parseDouble(min)) {
					throw new IllegalArgumentException("The value '" + value + "' is smaller than the allowed '" + min + "'");
				}
			}
			else {
				if (((Number) value).longValue() < Long.parseLong(min)) {
					throw new IllegalArgumentException("The value '" + value + "' is smaller than the allowed '" + min + "'");
				}
			}
		}
		else {
			String string = toString(value);
			if (string.length() < Integer.parseInt(min)) {
				throw new IllegalArgumentException("The value '" + value + "' has fewer characters than allowed: " + min);
			}
		}
	}
	
	private void pattern(Object value, String pattern) {
		String string = toString(value);
		if (pattern == null || pattern.trim().isEmpty()) {
			throw new IllegalStateException("The pattern is empty");
		}
		else {
			pattern = pattern.trim();
		}
		if (pattern.equals("word")) {
			pattern = "[\\w]+";
		}
		else if (pattern.equals("whitespace")) {
			pattern = "[\\s]+";
		}
		else if (pattern.equals("number")) {
			pattern = "[0-9]+|[0-9]+.[0-9]+";
		}
		if (!string.matches(pattern)) {
			throw new IllegalArgumentException("The value '" + string + "' does not match the required pattern '" + pattern + "'");
		}
	}

	private void enumeration(Object value, String enumeration) {
		String string = toString(value);
		if (enumeration == null || enumeration.trim().isEmpty()) {
			throw new IllegalStateException("The pattern is empty");
		}
		else {
			enumeration = enumeration.trim();
		}
		Collection<String> values;
		if (enumeration.equals("language")) {
			values = languages;
		}
		else if (enumeration.equals("country")) {
			values = countries;
		}
		else if (enumeration.equals("timezone")) {
			values = timezones;
		}
		else if (enumeration.equals("charset")) {
			values = charsets;
		}
		else {
			values = Arrays.asList(enumeration.split("[\\s]*,[\\s]*"));
		}
		if (!values.contains(string)) {
			throw new IllegalArgumentException("The value '" + string + "' does not belong to the expected enumeration");
		}
	}
	
	private static String toString(Object value) {
		return ConverterFactory.getInstance().getConverter().convert(value, String.class);
	}

	@Override
	public void before(Executor executor) {
		parent.before(executor);
	}

	@Override
	public void validated(GlueValidation...validations) {
		parent.validated(validations);
	}

	@Override
	public void print(Object...messages) {
		parent.print(messages);		
	}

	@Override
	public void end(Script script, Date started, Date stopped, Exception exception) {
		parent.end(script, started, stopped, exception);
	}

	@Override
	public boolean shouldExecute(Executor executor) {
		return parent.shouldExecute(executor);
	}

}
