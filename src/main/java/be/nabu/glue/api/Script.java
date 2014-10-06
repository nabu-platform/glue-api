package be.nabu.glue.api;

import java.io.IOException;
import java.nio.charset.Charset;

import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.ResourceContainer;

public interface Script {
	/**
	 * The repository it belongs to
	 */
	public ScriptRepository getRepository();
	/**
	 * The namespace this script belongs to
	 * @return
	 */
	public String getNamespace();
	/**
	 * The name of the script, if you call getRepository().getScript(getName()) it should return this script
	 */
	public String getName();
	/**
	 * The root of the execution for the script
	 */
	public ExecutorGroup getRoot();
	/**
	 * The charset used to parse this script
	 */
	public Charset getCharset();
	/**
	 * The parsed used to parse this script
	 */
	public Parser getParser();
	/**
	 * The source code of the script that was given to the parser
	 */
	public ReadableResource getSource() throws IOException;
	/**
	 * The container where resources belonging to this script would be in
	 */
	public ResourceContainer<?> getResources() throws IOException;
}
