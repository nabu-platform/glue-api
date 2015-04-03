package be.nabu.glue.impl;

import java.io.Closeable;
import java.io.IOException;

import be.nabu.glue.api.Transactionable;

public class TransactionalCloseable implements Transactionable {

	private Closeable closeable;

	public TransactionalCloseable(Closeable closeable) {
		this.closeable = closeable;
	}
	
	@Override
	public void commit() throws IOException {
		closeable.close();
	}

	@Override
	public void rollback() throws IOException {
		closeable.close();
	}

	@Override
	public int hashCode() {
		return closeable.hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		return object instanceof TransactionalCloseable && 
			((TransactionalCloseable) object).closeable.equals(closeable);
	}
}
