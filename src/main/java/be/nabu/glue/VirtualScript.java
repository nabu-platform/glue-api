package be.nabu.glue;

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

	public VirtualScript(Script parent, String source) throws IOException, ParseException {
		this.parent = parent;
		this.source = source;
		this.root = parent.getParser().parse(new StringReader(source));
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
		return parent.getParser();
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
