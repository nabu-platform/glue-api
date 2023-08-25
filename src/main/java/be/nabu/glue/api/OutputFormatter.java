package be.nabu.glue.api;

import java.util.Date;

import be.nabu.glue.api.runs.GlueAttachment;
import be.nabu.glue.api.runs.GlueValidation;

public interface OutputFormatter {
	public void start(Script script);
	public void before(Executor executor);
	public void after(Executor executor);
	public void validated(GlueValidation...validations);
	public void print(Object...messages);
	public void end(Script script, Date started, Date stopped, Exception exception);
	public boolean shouldExecute(Executor executor);
	
	public default OutputFormatter getParent() {
		return null;
	}
	public default void attached(GlueAttachment...attachments) {
		// do nothing...
	}
}
