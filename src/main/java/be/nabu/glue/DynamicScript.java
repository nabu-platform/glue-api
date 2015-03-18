package be.nabu.glue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.Parser;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptRepository;

public class DynamicScript implements Script {

	private ScriptRepository repository;
	private ExecutorGroup root;
	private Parser parser;

	public DynamicScript(ScriptRepository repository, Parser parser) {
		this.repository = repository;
		this.parser = parser;
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
		return null;
	}

	@Override
	public String getName() {
		return "runtime$" + hashCode();
	}

	@Override
	public ExecutorGroup getRoot() throws IOException, ParseException {
		return root;
	}

	@Override
	public Charset getCharset() {
		return Charset.defaultCharset();
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
}
