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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import be.nabu.glue.api.ExecutionEnvironment;

public class SimpleExecutionEnvironment implements ExecutionEnvironment {

	private String name;
	private Map<String, String> parameters = new HashMap<String, String>();
	private static Properties properties;

	public SimpleExecutionEnvironment(String name) throws IOException {
		this.name = name.toLowerCase();
		// first set all generic parameters
		this.parameters.putAll(getEnvironmentProperties("*"));
		// then set all specific parameters
		this.parameters.putAll(getEnvironmentProperties(this.name));
	}
	
	public SimpleExecutionEnvironment(String name, Map<String, String> parameters) {
		this.name = name;
		this.parameters.putAll(parameters);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	private Map<String, String> getEnvironmentProperties(String environment) throws IOException {
		Map<String, String> properties = new HashMap<String, String>();
		for (Object key : getProperties().keySet()) {
			if (key.toString().trim().toLowerCase().startsWith(environment + ".")) {
				properties.put(key.toString().trim().substring(environment.length() + 1), getProperties().getProperty(key.toString()).trim());
			}
		}
		return properties;
	}

	public static SortedSet<String> getEnvironments() throws IOException {
		SortedSet<String> environments = new TreeSet<String>();
		for (Object key : getProperties().keySet()) {
			String propertyName = key.toString().trim().toLowerCase();
			int index = propertyName.indexOf('.');
			String name = index < 0 ? propertyName : propertyName.substring(0, index);
			if (!"*".equals(name)) {
				environments.add(name);
			}
		}
		return environments;
	}
	
	private static Properties getProperties() throws IOException {
		// we look for files in different places and merge the results in a pre-defined order allowing you to create local settings that overwrite the default distributed ones
		if (properties == null) {
			properties = new Properties();
			// check the installation directory (if possible)
			String installationDirectory = System.getProperty("glue", System.getenv("GLUE"));
			File installationFile = installationDirectory == null ? null : new File(installationDirectory, ".glue");
			if (installationFile != null && installationFile.exists()) {
				properties.putAll(parse(installationFile));
			}
			// if we can't get the installation directory, get the current directory
			else {
				// check the current directory where glue is running, this may or may not be the installation folder
				// if it _is_ the same as the installation folder, don't reload or you might overwrite settings in the installation directory
				File localFile = new File(System.getProperty("user.dir"), ".glue");
				if (localFile.exists()) {
					properties.putAll(parse(localFile));
				}
			}
			// check the home folder last, any custom changes here take priority over the previous ones allowing for local overrides
			File homeFile = new File(System.getProperty("user.home"), ".glue");
			if (homeFile.exists()) {
				properties.putAll(parse(homeFile));
			}
		}
		return properties;
	}
	
	public static void loadProperties(InputStream input) {
		Properties properties = new Properties();
		try {
			properties.load(input);
			getProperties().putAll(properties);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Properties parse(File file) throws IOException {
		Properties properties = new Properties();
		FileInputStream input = new FileInputStream(file);
		try {
			properties.load(input);
		}
		finally {
			input.close();
		}
		return properties;
	}
}
