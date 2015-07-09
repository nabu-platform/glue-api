package be.nabu.glue.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface GlueParam {
	/**
	 * The name of the parameter. This may be used in named parameters so make sure it is a valid name according to glue syntax
	 */
	public String name() default "";
	/**
	 * The description of the parameter shown as text to the user
	 */
	public String description() default "";
	/**
	 * If the value is optional, describe what the optional value is so people know what happens if they leave it empty
	 */
	public String defaultValue() default "";
}
