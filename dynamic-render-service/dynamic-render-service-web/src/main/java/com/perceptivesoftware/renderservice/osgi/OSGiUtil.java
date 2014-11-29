package com.perceptivesoftware.renderservice.osgi;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;

public class OSGiUtil {

	public static <T> T getService(BundleContext bundleContext, Class<?> serviceInterface) {
		return (T) bundleContext.getService(bundleContext.getServiceReference(serviceInterface.getName()));
	}

	public static <T> T getService(ServletContext servletContext, Class<?> serviceInterface) {
		BundleContext bundleContext = (BundleContext) servletContext.getAttribute("osgi-bundlecontext");
		if (bundleContext == null) {
			throw new IllegalStateException("osgi-bundlecontext not registered");
		}
		return getService(bundleContext, serviceInterface);
	}

	public static BundleContext getBundleContext(ServletContext servletContext) {
		BundleContext bundleContext = (BundleContext) servletContext.getAttribute("osgi-bundlecontext");
		if (bundleContext == null) {
			throw new IllegalStateException("OSGi bundle context has not been registered.");
		}
		return bundleContext;
	}
}
