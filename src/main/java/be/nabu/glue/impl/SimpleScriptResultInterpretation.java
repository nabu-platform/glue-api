package be.nabu.glue.impl;

import be.nabu.glue.api.runs.ScriptResultInterpretation;

public class SimpleScriptResultInterpretation implements ScriptResultInterpretation {

	private double actualVariance, allowedVariance;
	
	public SimpleScriptResultInterpretation(double actualVariance, double allowedVariance) {
		this.actualVariance = actualVariance;
		this.allowedVariance = allowedVariance;
	}

	@Override
	public double getActualVariance() {
		return actualVariance;
	}

	@Override
	public double getAllowedVariance() {
		return allowedVariance;
	}

}
