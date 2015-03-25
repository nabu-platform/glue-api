package be.nabu.glue.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
			environments.add(index < 0 ? propertyName : propertyName.substring(0, index));
		}
		return environments;
	}
	
	private static Properties getProperties() throws IOException {
		if (properties == null) {
			properties = new Properties();
			// first check the current directory where glue is running
			File file = new File(System.getProperty("user.dir"), ".glue");
			// if it does not exist, check the home folder
			if (!file.exists()) {
				file = new File(System.getProperty("user.home"), ".glue");
			}
			if (file.exists()) {
				FileInputStream input = new FileInputStream(file);
				try {
					properties.load(input);
				}
				finally {
					input.close();
				}
			}
		}
		return properties;
	}
}
