package be.nabu.glue.api;

import java.io.IOException;
import java.util.Map;

import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;

public interface ExecutionContext {
	/**
	 * The environment it is running in
	 */
	public ExecutionEnvironment getExecutionEnvironment();
	/**
	 * This keeps track of all the variables that exist in this context
	 */
	public Map<String, Object> getPipeline();
	/**
	 * Whether or not you are debugging
	 */
	public boolean isDebug();
	/**
	 * This allows the executor to register itself before it evaluates. 
	 * Because the execution is internalized (the executors run themselves and possibly other executors) and not externalized (driven by an external force) we need a way to know which step is executing
	 */
	public Executor getCurrent();
	public void setCurrent(Executor executor);
	/**
	 * This allows for breakpoint manipulation. Any engine that supports it will stop before executing the step that has this breakpoint id
	 */
	public String getBreakpoint();
	public void setBreakpoint(String id);
	/**
	 * This allows you to use custom label evaluation to choose how you want to decide which optional lines should be run
	 * It defaults to checking if the label matches the environment
	 */
	public LabelEvaluator getLabelEvaluator();
	/**
	 * This allows you to fetch content from the execution context
	 * This is mostly to load resources 
	 */
	public ReadableContainer<ByteBuffer> getContent(String name) throws IOException;
}
