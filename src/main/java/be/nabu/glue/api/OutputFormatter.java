package be.nabu.glue.api;

import java.util.Date;

import be.nabu.glue.api.runs.Validation;

public interface OutputFormatter {
	public void start(Script script);
	public void before(Executor executor);
	public void after(Executor executor);
	public void validated(Validation...validations);
	public void print(Object...messages);
	public void end(Script script, Date started, Date stopped, Exception exception);
}
