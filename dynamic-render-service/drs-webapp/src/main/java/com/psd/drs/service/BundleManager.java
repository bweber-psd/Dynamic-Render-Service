package com.psd.drs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.psd.rendering.drs.osgi.api.RenderService;
import com.psd.rendering.drs.osgi.api.RenderServiceTracker;
import com.saperion.connector.render.engine.RenderEngine;

@Stateless
public class BundleManager {
	
	@Resource
	private BundleContext context;
	
	@Inject
	private Logger log;

//	private Collection<ServiceReference<Activator>> bundleContext;
	
	public List<String> getAllBundles() {
		Bundle[] bundles = context.getBundles();
		try {
			ServiceReference<?> serviceReference = context.getServiceReference(RenderService.class.getName());
			if (serviceReference != null) {
				log.info("Service reference: " + serviceReference.getBundle().getSymbolicName());
			}
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		List<String> bundleNames = new ArrayList<String>();
		for (Bundle bundle : bundles) {
			bundleNames.add(bundle.getSymbolicName());
			log.info("Bundle: " + bundle + " " + bundle.getRegisteredServices() + " " + bundle.getLocation());
			
		}
		return bundleNames;
	}
	
	public RenderService getRenderService() {
		ServiceReference<RenderServiceTracker> serviceReference = (ServiceReference<RenderServiceTracker>) context.getServiceReference(RenderServiceTracker.class.getName());
		RenderServiceTracker renderServiceTracker = context.getService(serviceReference);
		RenderService renderService = context.getService(serviceReference).getService(null);
		System.out.println("Render Service Bundle count: " + renderServiceTracker.getServiceCount());
		log.info("Render Service Bundle count: " + renderServiceTracker.getServiceCount());
		RenderEngine renderEngine = renderService.getRenderEngine();
		return context.getService(serviceReference).getService(null);
	//	RenderServiceTracker serviceTracker = (RenderServiceTracker) context.getService(serviceReference).getService(null);
	//	return serviceTracker.getService(null);
	}
	

}
