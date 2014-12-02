package com.perceptivesoftware.renderservice.listener;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import com.perceptivesoftware.renderservice.cache.RenderServiceCacheManager;
import com.perceptivesoftware.renderservice.configuration.ConfigurationMBean;
import com.perceptivesoftware.renderservice.configuration.RenderServiceConfiguration;
import com.perceptivesoftware.renderservice.render.RenderThreadPool;
import com.perceptivesoftware.renderservice.util.ApplicationVersion;
import com.saperion.common.logging.Logger;
import com.saperion.connector.renditions.util.TemporaryFileManager;

/**
 * Context listener that is notified when the application's servlet context is initialized and
 * destroyed.
 */
@WebListener
public class ContextListener implements ServletContextListener {

	private static final Logger LOGGER = Logger.getLogger(ContextListener.class);
	private static final List<ObjectName> REGISTERED_MBEAN_NAMES = new ArrayList<ObjectName>();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOGGER.info("Render Service application version " + ApplicationVersion.getVersionString());
		RenderServiceConfiguration.initialize(getPath(sce.getServletContext()));
		RenderThreadPool.initialize();
		String contextPath = sce.getServletContext().getContextPath();
		RenderServiceCacheManager.getInstance().initialize(contextPath);
		registerConfigMBean(contextPath);
	}

	private String getPath(ServletContext context) {
		String testfile = "/index.jsp";
		String realPath = context.getRealPath(testfile);
		realPath = realPath.replace(File.separatorChar + "index.jsp", "");
		return realPath;
	}

	private static void registerConfigMBean(String contextPath) {

		MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

		try {
			ObjectName name = getObjectName(contextPath);
			platformMBeanServer.registerMBean(new ConfigurationMBean(), name);
			REGISTERED_MBEAN_NAMES.add(name);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | MalformedObjectNameException e) {
			LOGGER.logThrowableWithStacktrace("Registering configuration MBean failed.", e);
		}
	}

	private static ObjectName getObjectName(String contextPath) throws MalformedObjectNameException {
		return new ObjectName("Saperion:type=RenderServiceConfiguration,category=" + contextPath);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		RenderThreadPool.shutdown();
		TemporaryFileManager.shutdownDeleteAll();
		RenderServiceCacheManager.getInstance().shutdown();
		unregisterConfigMBean();
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		loggerContext.stop();
	}

	private void unregisterConfigMBean() {
		MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

		for (ObjectName name : REGISTERED_MBEAN_NAMES) {
			try {
				platformMBeanServer.unregisterMBean(name);
			} catch (InstanceNotFoundException | MBeanRegistrationException e) {
				LOGGER.logThrowableWithStacktrace("Unregistering configuration MBean failed.", e);
			}
		}

		REGISTERED_MBEAN_NAMES.clear();
	}
}
