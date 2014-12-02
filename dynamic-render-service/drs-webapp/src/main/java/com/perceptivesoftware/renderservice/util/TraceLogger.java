package com.perceptivesoftware.renderservice.util;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.saperion.common.logging.Logger;

/**
 * Logs method-entry and -exit with time needed for execution.
 */
@Aspect
public class TraceLogger {

	@Around(value = "execution(@com.perceptivesoftware.renderservice.annotation.TraceLog * *(..))")
	// CHECKSTYLE:OFF
	public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		// CHECKSTYLE:ON
		Signature signature = joinPoint.getSignature();
		Class<?> clazz = signature.getDeclaringType();
		Logger logger = Logger.getLogger(clazz);

		if (logger.isTraceEnabled()) {
			String shortSignature = signature.toShortString();
			long start = System.currentTimeMillis();

			StringBuilder message = new StringBuilder();
			message.append("Entering method ").append(shortSignature).append(" with parameters ")
					.append(Arrays.toString(joinPoint.getArgs()));

			logger.trace(message.toString());

			Object result = joinPoint.proceed();

			long duration = System.currentTimeMillis() - start;

			message = new StringBuilder();
			message.append("Leaving method ").append(shortSignature).append(" after ")
					.append(duration).append("ms");

			logger.trace(message.toString());

			return result;
		} else {
			return joinPoint.proceed();
		}
	}
}
