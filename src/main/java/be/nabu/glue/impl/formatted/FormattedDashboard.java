package be.nabu.glue.impl.formatted;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import be.nabu.glue.api.runs.ScriptResult;
import be.nabu.glue.api.runs.Validation.Level;

@XmlRootElement(name = "dashboard")
@XmlType(propOrder = { "started", "stopped", "amountRun", "amountSuccessful", "amountError", "amountCritical", "results" })
public class FormattedDashboard {
	
	private int amountRun, amountSuccessful, amountError, amountCritical;
	private List<FormattedDashboardEntry> results;
	private Date started, stopped;
	
	public static FormattedDashboard format(ScriptResult...results) {
		FormattedDashboard dashboard = new FormattedDashboard();
		List<FormattedDashboardEntry> entries = new ArrayList<FormattedDashboardEntry>();
		int amountSuccessful = 0,
			amountError = 0,
			amountCritical = 0;
		Date started = null, stopped = null;
		for (ScriptResult result : results) {
			switch(result.getResultLevel()) {
				case CRITICAL: amountCritical++; break;
				case INFO: amountSuccessful++; break;
				case ERROR: amountError++; break;
				case WARN: amountSuccessful++; break;
			}
			FormattedDashboardEntry entry = new FormattedDashboardEntry();
			entry.setLevel(result.getResultLevel());
			entry.setName(result.getScript().getName());
			entry.setNamespace(result.getScript().getNamespace());
			entry.setStarted(result.getStarted());
			entry.setStopped(result.getStopped());
			entry.setEnvironment(result.getEnvironment().getName());
			entries.add(entry);
			if (started == null || (result.getStarted() != null && result.getStarted().before(started))) {
				started = result.getStarted();
			}
			if (stopped == null || (result.getStopped() != null && result.getStopped().after(stopped))) {
				stopped = result.getStopped();
			}
		}
		dashboard.setStarted(started);
		dashboard.setStopped(stopped);
		dashboard.setAmountCritical(amountCritical);
		dashboard.setAmountError(amountError);
		dashboard.setAmountSuccessful(amountSuccessful);
		dashboard.setAmountRun(amountCritical + amountError + amountSuccessful);
		dashboard.setResults(entries);
		return dashboard;
	}
	
	public int getAmountRun() {
		return amountRun;
	}
	public void setAmountRun(int amountRun) {
		this.amountRun = amountRun;
	}
	public int getAmountSuccessful() {
		return amountSuccessful;
	}
	public void setAmountSuccessful(int amountSuccessful) {
		this.amountSuccessful = amountSuccessful;
	}
	public int getAmountError() {
		return amountError;
	}
	public void setAmountError(int amountError) {
		this.amountError = amountError;
	}
	public int getAmountCritical() {
		return amountCritical;
	}
	public void setAmountCritical(int amountCritical) {
		this.amountCritical = amountCritical;
	}
	public List<FormattedDashboardEntry> getResults() {
		return results;
	}
	public void setResults(List<FormattedDashboardEntry> results) {
		this.results = results;
	}
	public Date getStarted() {
		return started;
	}
	public void setStarted(Date started) {
		this.started = started;
	}
	public Date getStopped() {
		return stopped;
	}
	public void setStopped(Date stopped) {
		this.stopped = stopped;
	}

	public static class FormattedDashboardEntry {
		private String namespace, name, environment;
		private Level level;
		private Date started, stopped;
		public String getNamespace() {
			return namespace;
		}
		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Level getLevel() {
			return level;
		}
		public void setLevel(Level level) {
			this.level = level;
		}
		public Date getStarted() {
			return started;
		}
		public void setStarted(Date started) {
			this.started = started;
		}
		public Date getStopped() {
			return stopped;
		}
		public void setStopped(Date stopped) {
			this.stopped = stopped;
		}
		public String getEnvironment() {
			return environment;
		}
		public void setEnvironment(String environment) {
			this.environment = environment;
		}
	}
	
	public static FormattedDashboard unmarshal(InputStream input) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(FormattedDashboard.class);
		return (FormattedDashboard) context.createUnmarshaller().unmarshal(input);
	}
	
	public void marshal(OutputStream output) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(FormattedDashboard.class);
		context.createMarshaller().marshal(this, output);
	}
}
