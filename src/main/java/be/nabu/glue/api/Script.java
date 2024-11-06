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

package be.nabu.glue.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;

/**
 * The script should return an iterator over the resources attached to it
 */
public interface Script extends Iterable<String> {
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
	public ExecutorGroup getRoot() throws IOException, ParseException;
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
	public InputStream getSource() throws IOException;
	/**
	 * The container where resources belonging to this script would be in
	 */
	public InputStream getResource(String name) throws IOException;
}
