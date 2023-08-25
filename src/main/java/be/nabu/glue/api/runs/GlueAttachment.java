package be.nabu.glue.api.runs;

import java.util.Date;

import be.nabu.glue.api.Executor;

public interface GlueAttachment {
	// the executor that generated the attachment
	public Date getCreated();
	public Executor getExecutor();
	public String getName();
	public byte [] getContent();
	public String getMessage();
	public String getContentType();
}
