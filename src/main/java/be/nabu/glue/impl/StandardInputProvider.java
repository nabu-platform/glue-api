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
		if (System.console() != null) {
			if (secret) {
				return new String(System.console().readPassword());
			}
			else {
				return System.console().readLine();
			}
		}
		else {
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		}
	}

}
