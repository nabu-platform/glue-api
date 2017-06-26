package be.nabu.glue.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import be.nabu.glue.api.ExecutionContext;
import be.nabu.glue.api.ExecutionEnvironment;
import be.nabu.glue.api.Executor;
import be.nabu.glue.api.LabelEvaluator;

public class ForkedExecutionContext implements ExecutionContext {

	private ExecutionContext parent;
	private Executor current;
	private int breakCount;
	private Map<String, Object> pipeline;

	public ForkedExecutionContext(ExecutionContext parent) {
		this(parent, false);
	}
	
	public ForkedExecutionContext(ExecutionContext parent, boolean localPipeline) {
		this.parent = parent;
		this.pipeline = localPipeline ? new HashMap<String, Object>(parent.getPipeline()) : parent.getPipeline();
	}
	
	public ForkedExecutionContext(ExecutionContext parent, Map<String, Object> pipeline) {
		this.parent = parent;
		this.pipeline = pipeline;
	}
	
	@Override
	public ExecutionEnvironment getExecutionEnvironment() {
		return parent.getExecutionEnvironment();
	}

	@Override
	public Map<String, Object> getPipeline() {
		return pipeline;
	}

	@Override
	public boolean isDebug() {
		return parent.isDebug();
	}

	@Override
	public boolean isTrace() {
		return parent.isTrace();
	}

	@Override
	public Executor getCurrent() {
		return current;
	}

	@Override
	public void setCurrent(Executor executor) {
		this.current = executor;
	}

	@Override
	public Set<String> getBreakpoints() {
		return parent.getBreakpoints();
	}

	@Override
	public void addBreakpoint(String...id) {
		parent.addBreakpoint(id);
	}

	@Override
	public void removeBreakpoint(String id) {
		parent.removeBreakpoint(id);
	}

	@Override
	public void removeBreakpoints() {
		parent.removeBreakpoints();
	}

	@Override
	public LabelEvaluator getLabelEvaluator() {
		return parent.getLabelEvaluator();
	}

	@Override
	public InputStream getContent(String name) throws IOException {
		return parent.getContent(name);
	}

	@Override
	public void setTrace(boolean trace) {
		parent.setTrace(trace);
	}

	@Override
	public int getBreakCount() {
		return breakCount;
	}

	@Override
	public void incrementBreakCount(int breakCount) {
		this.breakCount += breakCount;
	}

	@Override
	public Principal getPrincipal() {
		return parent.getPrincipal();
	}

	public ExecutionContext getParent() {
		return parent;
	}
	
}
