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
