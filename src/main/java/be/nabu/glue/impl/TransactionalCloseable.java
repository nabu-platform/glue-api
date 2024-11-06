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
