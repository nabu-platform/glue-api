package be.nabu.glue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

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
		return new ArrayList<String>().iterator();
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
		return null;
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
}
