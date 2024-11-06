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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.Parser;
import be.nabu.glue.api.ParserProvider;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptRepository;

public class DynamicScript implements Script {

	private ScriptRepository repository;
	private ExecutorGroup root;
	private Parser parser;
	private Charset charset = Charset.defaultCharset();
	private String name, namespace;
	private Map<String, byte[]> resources = new HashMap<String, byte[]>();

	public DynamicScript(ScriptRepository repository, Parser parser) {
		this.repository = repository;
		this.parser = parser;
	}
	
	public DynamicScript(ScriptRepository repository, Parser parser, String content) throws IOException, ParseException {
		this.repository = repository;
		this.parser = parser;
		this.root = parser.parse(new StringReader(content));
	}
	
	public DynamicScript(ScriptRepository repository, ParserProvider parserProvider, String content) throws IOException, ParseException {
		this(repository, parserProvider.newParser(repository, "dynamic.glue"), content);
	}
	
	@Override
	public Iterator<String> iterator() {
		return resources.keySet().iterator();
	}

	@Override
	public ScriptRepository getRepository() {
		return repository;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getName() {
		return name == null ? "runtime$" + hashCode() : name;
	}

	@Override
	public ExecutorGroup getRoot() throws IOException, ParseException {
		return root;
	}

	@Override
	public Charset getCharset() {
		return charset;
	}

	@Override
	public Parser getParser() {
		return parser;
	}

	@Override
	public InputStream getSource() throws IOException {
		return null;
	}

	@Override
	public InputStream getResource(String name) throws IOException {
		byte[] content = resources.get(name);
		return content == null ? null : new ByteArrayInputStream(content);
	}

	public void setRoot(ExecutorGroup root) {
		this.root = root;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Map<String, byte[]> getResources() {
		return resources;
	}
	
}
