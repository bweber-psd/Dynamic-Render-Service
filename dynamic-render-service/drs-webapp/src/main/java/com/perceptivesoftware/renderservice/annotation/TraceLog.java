package com.perceptivesoftware.renderservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables trace-logging for the annotated method. If the TRACE level is enabled, each method-
 * entry and exit will be logged along with the time needed for the execution.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface TraceLog {

}
