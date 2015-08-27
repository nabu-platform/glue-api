package be.nabu.glue.impl.formatted;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import be.nabu.glue.api.runs.ScriptResult;
import be.nabu.glue.api.runs.ScriptResultInterpretation;
import be.nabu.glue.api.runs.GlueValidation;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

@XmlRootElement(name = "result")
@XmlType(propOrder = { "severity", "namespace", "name", "environment", "started", "stopped", "actualVariance", "allowedVariance", "amountValidations", "amountSuccessful", "amountError", "amountCritical", "validations", "exception", "log" })
public class FormattedScriptResult {
	private List<FormattedValidation> validations;
	private Severity severity;
	private String name, namespace, exception, log, environment;
	private Date started, stopped;
	private int amountValidations, amountSuccessful, amountError, amountCritical;
	private Double actualVariance, allowedVariance;
	
	public static FormattedScriptResult format(ScriptResult result, ScriptResultInterpretation interpretation) {
		FormattedScriptResult formatted = new FormattedScriptResult();
		formatted.setSeverity(result.getResultLevel());
		formatted.setNamespace(result.getScript().getNamespace());
		formatted.setName(result.getScript().getName());
		formatted.setStarted(result.getStarted());
		formatted.setStopped(result.getStopped());
		formatted.setEnvironment(result.getEnvironment().getName());
		List<FormattedValidation> validations = new ArrayList<FormattedValidation>();
		int amountSuccessful = 0, amountError = 0, amountCritical = 0;
		for (GlueValidation validation : result.getValidations()) {
			switch(validation.getSeverity()) {
				case CRITICAL: amountCritical++; break;
				case ERROR: amountError++; break;
				default: amountSuccessful++;
			}
			validations.add(FormattedValidation.format(validation));
		}
		formatted.setAmountCritical(amountCritical);
		formatted.setAmountError(amountError);
		formatted.setAmountSuccessful(amountSuccessful);
		formatted.setAmountValidations(amountCritical + amountError + amountSuccessful);
		formatted.setValidations(validations);
		if (result.getException() != null) {
			StringWriter output = new StringWriter();
			PrintWriter printer = new PrintWriter(output);
			result.getException().printStackTrace(printer);
			printer.flush();
			formatted.setException(output.toString());
		}
		formatted.setLog(result.getLog());
		if (interpretation != null) {
			formatted.setActualVariance(interpretation.getActualVariance());
			formatted.setAllowedVariance(interpretation.getAllowedVariance());
		}
		return formatted;
	}
	
	public List<FormattedValidation> getValidations() {
		return validations;
	}
	public void setValidations(List<FormattedValidation> validations) {
		this.validations = validations;
	}
	/**
	 * This name is for legacy reasons, it was called level before it merged with the validation API
	 */
	@XmlElement(name = "level")
	public Severity getSeverity() {
		return severity;
	}
	public void setSeverity(Severity level) {
		this.severity = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
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
	public int getAmountValidations() {
		return amountValidations;
	}
	public void setAmountValidations(int amountValidations) {
		this.amountValidations = amountValidations;
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
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public static FormattedScriptResult unmarshal(InputStream input) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(FormattedScriptResult.class);
		return (FormattedScriptResult) context.createUnmarshaller().unmarshal(input);
	}
	
	public void marshal(OutputStream output) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(FormattedScriptResult.class);
		context.createMarshaller().marshal(this, output);
	}

	public Double getActualVariance() {
		return actualVariance;
	}

	public void setActualVariance(Double actualVariance) {
		this.actualVariance = actualVariance;
	}

	public Double getAllowedVariance() {
		return allowedVariance;
	}

	public void setAllowedVariance(Double allowedVariance) {
		this.allowedVariance = allowedVariance;
	}
}
