package be.nabu.glue.impl;

import java.io.IOException;
import java.text.ParseException;

import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptFilter;

public class TestCaseFilter implements ScriptFilter {
	@Override
	public boolean accept(Script script) {
		try {
			if (script.getRoot() != null && script.getRoot().getContext().getAnnotations().containsKey("testcase")) {
				String string = script.getRoot().getContext().getAnnotations().get("testcase");
				return string == null || string.equalsIgnoreCase("true");
			}
			return false;
		}
		catch (IOException e) {
			e.printStackTrace(System.err);
			return false;
		}
		catch (ParseException e) {
			e.printStackTrace(System.err);
			return false;
		}
	}
}
