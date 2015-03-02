package be.nabu.glue.api.runs;

public interface ScriptResultInterpretation {
	/**
	 * Gets the variance of this script result versus a reference point [0,1[
	 */
	public double getActualVariance();
	/**
	 * Gets the allowed variance for this script result [0,1[
	 * This is both up and under, so if the allowed variance is 0.2 and the variance is -0.3, it will still be flagged 
	 * but likely with less urgency than when it is +0.3 
	 */
	public double getAllowedVariance();
}
