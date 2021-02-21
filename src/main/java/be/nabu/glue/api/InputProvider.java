package be.nabu.glue.api;

import java.io.IOException;

// allows you to interact with whomever is running the script
public interface InputProvider {
	public String input(String message, boolean secret, String defaultAnswer) throws IOException;
}
