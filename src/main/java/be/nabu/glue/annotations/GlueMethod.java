package be.nabu.glue.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GlueMethod {
	public String description() default "";
	public String returns() default "";
	public double version() default -1;
	// restricted methods are not available in sandbox mode
	public boolean restricted() default false;
}
