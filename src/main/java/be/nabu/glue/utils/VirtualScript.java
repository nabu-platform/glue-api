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

package be.nabu.glue.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Iterator;

import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.Parser;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptRepository;

public class VirtualScript implements Script {

	private Script parent;
	private ExecutorGroup root;
	private String source;
	private Parser parser;

	public VirtualScript(Script parent, String source, Parser parser) throws IOException, ParseException {
		this.parser = parser;
		this.parent = parent;
		this.source = source;
		this.root = parser.parse(new StringReader(source));
	}
	
	public VirtualScript(Script parent, String source) throws IOException, ParseException {
		this(parent, source, parent.getParser());
	}
	
	@Override
	public ScriptRepository getRepository() {
		return parent.getRepository();
	}

	@Override
	public String getNamespace() {
		return parent.getNamespace();
	}

	@Override
	public String getName() {
		return parent.getName() + "$" + root.hashCode();
	}

	@Override
	public ExecutorGroup getRoot() throws IOException, ParseException {
		return root;
	}

	@Override
	public Charset getCharset() {
		return parent.getCharset();
	}

	@Override
	public Parser getParser() {
		return parser;
	}

	@Override
	public InputStream getSource() throws IOException {
		return new ByteArrayInputStream(source.getBytes(getCharset()));
	}

	@Override
	public InputStream getResource(String name) throws IOException {
		return parent.getResource(name);
	}

	@Override
	public Iterator<String> iterator() {
		return parent.iterator();
	}
}
