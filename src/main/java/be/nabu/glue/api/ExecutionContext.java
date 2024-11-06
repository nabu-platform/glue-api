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
import java.security.Principal;
import java.util.Map;
import java.util.Set;

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
	 * Whether or not we are tracing
	 */
	public boolean isTrace();
	/**
	 * This allows the executor to register itself before it evaluates. 
	 * Because the execution is internalized (the executors run themselves and possibly other executors) and not externalized (driven by an external force) we need a way to know which step is executing
	 */
	public Executor getCurrent();
	public void setCurrent(Executor executor);
	/**
	 * This allows for breakpoint manipulation. Any engine that supports it will stop before executing the step that has this breakpoint id
	 */
	public Set<String> getBreakpoints();
	public void addBreakpoint(String...id);
	public void removeBreakpoint(String id);
	public void removeBreakpoints();
	
	/**
	 * This allows you to use custom label evaluation to choose how you want to decide which optional lines should be run
	 * It defaults to checking if the label matches the environment
	 */
	public LabelEvaluator getLabelEvaluator();
	/**
	 * This allows you to fetch content from the execution context
	 * This is mostly to load resources 
	 */
	public InputStream getContent(String name) throws IOException;
	/**
	 * Can toggle trace
	 */
	public void setTrace(boolean trace);
	/**
	 * The break count allows you to break out of executors
	 */
	public int getBreakCount();
	public void incrementBreakCount(int breakCount);
	public Principal getPrincipal();
}
