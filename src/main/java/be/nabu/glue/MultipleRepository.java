package be.nabu.glue;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.nabu.glue.api.ParserProvider;
import be.nabu.glue.api.Script;
import be.nabu.glue.api.ScriptRepository;

public class MultipleRepository implements ScriptRepository {

	private List<ScriptRepository> repositories = new ArrayList<ScriptRepository>();
	private Map<String, Script> scriptsBySimpleName, scriptsByFullName;
	private ScriptRepository parent;
	
	public MultipleRepository(ScriptRepository parent, ScriptRepository...children) {
		this.parent = parent;
		add(children);
	}
	
	@Override
	public Iterator<Script> iterator() {
		return new ArrayList<Script>(getScriptsBySimpleName().values()).iterator();
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

	private void loadScripts() {
		Map<String, Script> scriptsBySimpleName = new HashMap<String, Script>();
		Map<String, Script> scriptsByFullName = new HashMap<String, Script>();
		for (ScriptRepository repository : repositories) {
			for (Script script : repository) {
				if (!scriptsByFullName.containsKey(ScriptUtils.getFullName(script))) {
					scriptsByFullName.put(ScriptUtils.getFullName(script), script);
					if (!scriptsBySimpleName.containsKey(script.getName())) {
						scriptsBySimpleName.put(script.getName(), script);
					}
				}
			}
		}
		this.scriptsBySimpleName = scriptsBySimpleName;
		this.scriptsByFullName = scriptsByFullName;
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

	@Override
	public Script getScript(String name) throws IOException, ParseException {
		// try the full name first
		Script script = getScriptsByFullName().get(name);
		// then the simple name
		if (script == null) {
			script = getScriptsBySimpleName().get(name);
		}
		// if still not found, check the target repositories, it may be unscannable
		if (script == null) {
			for (ScriptRepository repository : repositories) {
				script = repository.getScript(name);
				if (script != null) {
					synchronized(this) {
						scriptsByFullName.put(ScriptUtils.getFullName(script), script);
						scriptsBySimpleName.put(script.getName(), script);
					}
					break;
				}
			}
		}
		return script;
	}

	@Override
	public ParserProvider getParserProvider() {
		return repositories.size() > 0 ? repositories.get(0).getParserProvider() : null;
	}

	@Override
	public ScriptRepository getParent() {
		return parent;
	}
	
	public void add(ScriptRepository...repositories) {
		this.repositories.addAll(Arrays.asList(repositories));
		synchronized(this) {
			loadScripts();
		}
	}

	@Override
	public void refresh() throws IOException {
		for (ScriptRepository repository : repositories) {
			repository.refresh();
		}
		synchronized(this) {
			loadScripts();
		}
	}
	
	public void remove(ScriptRepository...repository) {
		if (this.repositories.removeAll(Arrays.asList(repositories))) {
			synchronized(this) {
				loadScripts();
			}	
		}
	}
}
