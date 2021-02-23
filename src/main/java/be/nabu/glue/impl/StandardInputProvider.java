package be.nabu.glue.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import be.nabu.glue.api.InputProvider;

public class StandardInputProvider implements InputProvider {

	@Override
	public String input(String message, boolean secret, String defaultValue) throws IOException {
		if (message != null) {
			System.out.print(message);
		}
		String result;
		if (System.console() != null) {
			if (secret) {
				result = new String(System.console().readPassword());
			}
			else {
				result = System.console().readLine();
			}
		}
		else {
			result = new BufferedReader(new InputStreamReader(System.in)).readLine();
		}
		if (result == null || result.trim().isEmpty()) {
			result = defaultValue;
		}
		return result;
	}

}
