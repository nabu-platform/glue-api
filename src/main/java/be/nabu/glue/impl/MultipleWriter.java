package be.nabu.glue.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public class MultipleWriter extends Writer {

	private List<Writer> backends;
	private StringBuilder builder = new StringBuilder();
	
	public MultipleWriter(Writer...backends) {
		this.backends = Arrays.asList(backends);
	}
	
	@Override
	public void close() throws IOException {
		IOException exception = null;
		for (Writer writer : backends) {
			try {
				writer.close();
			}
			catch (IOException e) {
				exception = e;
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	@Override
	public void flush() throws IOException {
		IOException exception = null;
		for (Writer writer : backends) {
			try {
				writer.flush();
			}
			catch (IOException e) {
				exception = e;
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	@Override
	public void write(char[] arg0, int arg1, int arg2) throws IOException {
		builder.append(arg0, arg1, arg2);
		IOException exception = null;
		for (Writer writer : backends) {
			try {
				writer.write(arg0, arg1, arg2);
			}
			catch (IOException e) {
				exception = e;
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
