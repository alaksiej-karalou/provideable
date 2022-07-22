package com.github.aliakseikaraliou.provideable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Provideable {
	String packageName() default "";

	String name() default "";

	String methodName() default "";
}
