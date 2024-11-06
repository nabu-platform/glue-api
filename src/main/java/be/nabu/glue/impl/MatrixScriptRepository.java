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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.nabu.glue.api.ExecutorContext;
import be.nabu.glue.api.ExecutorGroup;
import be.nabu.glue.api.ParameterDescription;
import be.nabu.glue.api.Parser;
import be.nabu.glue.api.ParserProvider;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptFilter;
import be.nabu.glue.api.ScriptRepository;
import be.nabu.glue.utils.DynamicScript;
import be.nabu.glue.utils.ScriptUtils;

public class MatrixScriptRepository implements ScriptRepository {

	private ScriptFilter filter;
	private ScriptRepository parent;
	private Map<String, Script> scriptsBySimpleName, scriptsByFullName;
	private String scriptExtension = "glue";

	public MatrixScriptRepository(ScriptRepository parent, ScriptFilter filter) {
		this.parent = parent;
		this.filter = filter;
	}
	
	@Override
	public Iterator<Script> iterator() {
		return new ArrayList<Script>(getScriptsByFullName().values()).iterator();
	}

	@Override
	public Script getScript(String name) throws IOException, ParseException {
		// try the full name first
		Script script = getScriptsByFullName().get(name);
		// then the simple name
		if (script == null) {
			script = getScriptsBySimpleName().get(name);
		}
		// if still not found, check the parent repository, it may be unscannable
		if (script == null) {
			script = parent.getScript(name);
			if (script != null) {
				List<Script> children = filter == null || filter.accept(script) ? explode(script) : Arrays.asList(script);
				synchronized(this) {
					for (Script child : children) {
						scriptsByFullName.put(ScriptUtils.getFullName(child), child);
						if (!scriptsBySimpleName.containsKey(child.getName())) {
							scriptsBySimpleName.put(child.getName(), child);
						}
					}
				}
			}
		}
		return script;
	}

	@Override
	public ParserProvider getParserProvider() {
		return parent.getParserProvider();
	}

	@Override
	public ScriptRepository getParent() {
		return parent;
	}

	@Override
	public void refresh() throws IOException {
		parent.refresh();
		synchronized(this) {
			loadScripts();
		}
	}

	private void loadScripts() {
		Map<String, Script> scriptsBySimpleName = new HashMap<String, Script>();
		Map<String, Script> scriptsByFullName = new HashMap<String, Script>();
		for (Script script : parent) {
			List<Script> children = filter == null || filter.accept(script) ? explode(script) : Arrays.asList(script);
			for (Script child : children) {
				if (!scriptsByFullName.containsKey(ScriptUtils.getFullName(child))) {
					scriptsByFullName.put(ScriptUtils.getFullName(child), child);
					if (!scriptsBySimpleName.containsKey(child.getName())) {
						scriptsBySimpleName.put(child.getName(), child);
					}
				}
			}
		}
		this.scriptsBySimpleName = scriptsBySimpleName;
		this.scriptsByFullName = scriptsByFullName;
	}
	
	private List<Script> explode(Script script) {
		try {
			List<ParameterDescription> inputs = ScriptUtils.getInputs(script);
			// if there are no inputs, it is not a matrix script
			if (inputs.isEmpty()) {
				return Arrays.asList(script);
			}
			InputStream resource = getMatrix(script);
			if (resource == null) {
				return Arrays.asList(script);
			}
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				byte [] buffer = new byte[40960];
				int read = 0;
				while ((read = resource.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}
				String content = new String(output.toByteArray(), script.getCharset());
				Parser newParser = getParserProvider().newParser(this, script.getName() + "_matrix." + scriptExtension);
				// generate a new script per line
				List<String> descriptions = new ArrayList<String>();
				int counter = 1;
				Map<String, String> annotations = new HashMap<String, String>();
				List<Script> children = new ArrayList<Script>();
				for (String line : content.split("[\r\n]+")) {
					// this line provides a description of whatever is coming
					if (line.trim().startsWith("##")) {
						descriptions.add(line.trim().substring(2));
						continue;
					}
					// commented out line
					else if (line.trim().startsWith("#")) {
						continue;
					}
					else if (line.trim().startsWith("@")) {
						String [] parts = line.trim().substring(1).split("[\\s=]+", 2);
						annotations.put(parts[0], parts.length >= 2 ? parts[1] : null);
						continue;
					}
					StringBuilder builder = new StringBuilder();
					ExecutorContext context = script.getRoot().getContext();
					if (context != null) {
						if (context.getDescription() != null) {
							builder.append("##").append(context.getDescription().replaceAll("\n", "\n##"));
							builder.append("\n");
						}
						if (context.getComment() != null) {
							builder.append("#").append(context.getComment().replaceAll("\n", "\n#"));
							builder.append("\n");
						}
						if (context.getAnnotations() != null) {
							for (String key : context.getAnnotations().keySet()) {
								builder.append("@" + key);
								if (context.getAnnotations().get(key) != null && !context.getAnnotations().get(key).trim().isEmpty()) {
									builder.append(" ").append(context.getAnnotations().get(key));
								}
								builder.append("\n");
							}
						}
					}
					// add any description present in the csv file
					for (String description : descriptions) {
						builder.append("## " + description.trim()).append("\n");
					}
					descriptions.clear();
					
					// print the annotations (don't wipe them yet, need them later)
					for (String key : annotations.keySet()) {
						builder.append("@" + key);
						if (annotations.get(key) != null && !annotations.get(key).trim().isEmpty()) {
							builder.append(" ").append(annotations.get(key));
						}
						builder.append("\n");
					}
					
					// add empty line to signal that the above metadata is definitely at the script level
					builder.append("\n");
					
					// write the actual script call
					builder.append(ScriptUtils.getFullName(script)).append("(");
					boolean first = true;
					for (String value : line.split("[,;]+")) {
						if (first) {
							first = false;
						}
						else {
							builder.append(", ");
						}
						if (value.trim().isEmpty()) {
							builder.append("null");
						}
						else {
							builder.append("\"").append(value.trim().replaceAll("\"", "\\\"")).append("\"");
						}
					}
					builder.append(")");
					ExecutorGroup parse = newParser.parse(new StringReader(builder.toString()));
					DynamicScript dynamicScript = new DynamicScript(this, newParser);
					dynamicScript.setRoot(parse);
					dynamicScript.setCharset(script.getCharset());
					if (annotations.containsKey("name") && !annotations.get("name").trim().isEmpty()) {
						dynamicScript.setName(annotations.get("name"));
					}
					else {
						dynamicScript.setName(script.getName() + "_tc" + counter++);
					}
					dynamicScript.setNamespace(ScriptUtils.getFullName(script));
					children.add(dynamicScript);
					annotations.clear();
				}
				return children;
			}
			finally {
				resource.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			// ignore
			return Arrays.asList(script);
		}
	}
	
	protected InputStream getMatrix(Script script) throws IOException {
		return script.getResource("input.matrix.csv");
	}

	private Map<String, Script> getScriptsByFullName() {
		if (scriptsByFullName == null) {
			synchronized(this) {
				if (scriptsByFullName == null) {
					loadScripts();
				}
			}
		}
		return scriptsByFullName;
	}
	private Map<String, Script> getScriptsBySimpleName() {
		if (scriptsBySimpleName == null) {
			synchronized(this) {
				if (scriptsBySimpleName == null) {
					loadScripts();
				}
			}
		}
		return scriptsBySimpleName;
	}
}
