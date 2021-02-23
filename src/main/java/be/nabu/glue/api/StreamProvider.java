package be.nabu.glue.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamProvider {
	public OutputStream getErrorStream();
	public OutputStream getOutputStream();
	public InputStream getInputStream();
}
