package be.nabu.glue.api.runs;

import java.util.Date;

import be.nabu.glue.api.Executor;
import be.nabu.libs.validator.api.Validation;

public interface GlueValidation extends Validation<CallLocation> {
	/**
	 * The executor that performed the validation
	 */
	public Executor getExecutor();
	
}
