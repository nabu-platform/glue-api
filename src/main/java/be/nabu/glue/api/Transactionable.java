package be.nabu.glue.api;

import java.io.IOException;

public interface Transactionable {
	public void commit() throws IOException;
	public void rollback() throws IOException;
}
