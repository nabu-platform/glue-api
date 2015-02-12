package be.nabu.glue.api.runs;

import java.util.List;

import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.LabelEvaluator;
import be.nabu.glue.api.ScriptFilter;
import be.nabu.glue.api.ScriptRepository;

public interface ScriptRunner {
	public List<ScriptResult> run(ExecutionEnvironment environment, ScriptRepository repository, ScriptFilter filter, LabelEvaluator labelEvaluator);
}
