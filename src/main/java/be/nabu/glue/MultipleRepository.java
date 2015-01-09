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
	private Map<String, Script> scripts;
	private ScriptRepository parent;
	
	public MultipleRepository(ScriptRepository parent, ScriptRepository...children) {
		this.parent = parent;
		add(children);
	}
	
	@Override
	public Iterator<Script> iterator() {
		return getScripts().values().iterator();
	}
	
	private Map<String, Script> getScripts() {
		if (scripts == null) {
			scripts = new HashMap<String, Script>();
			for (ScriptRepository repository : repositories) {
				for (Script script : repository) {
					if (!scripts.containsKey(script.getName())) {
						scripts.put(script.getName(), script);
					}
				}
			}
		}
		return scripts;
	}

	@Override
	public Script getScript(String name) throws IOException, ParseException {
		if (!getScripts().containsKey(name)) {
			for (ScriptRepository repository : repositories) {
				Script script = repository.getScript(name);
				if (script != null) {
					scripts.put(name, script);
					break;
				}
			}
		}
		return getScripts().get(name);
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
	}
}
