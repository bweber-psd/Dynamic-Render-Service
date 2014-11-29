package com.psd.drs.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.psd.rendering.drs.osgi.api.RenderService;
import com.psd.rendering.drs.osgi.consumer.Activator;

@Stateless
public class BundleManager {
	
	@Resource
	private BundleContext context;
	
	@Inject
	private Logger log;

//	private Collection<ServiceReference<Activator>> bundleContext;
	
	public List<String> getAllBundles() {
		Bundle[] bundles = context.getBundles();
		Collection<ServiceReference<Activator>> bundleContext = null;
		try {
			ServiceReference<?> serviceReference = context.getServiceReference(RenderService.class.getName());
			if (serviceReference != null) {
				log.info("Service reference: " + serviceReference.getBundle().getSymbolicName());
			}
			if (bundleContext != null && !bundleContext.isEmpty()) {
				String bundleName = bundleContext.iterator().next().getBundle().getSymbolicName();
				log.info("Consumer Bundle: " + bundleName);
				return Collections.singletonList(bundleContext.iterator().next().getBundle().getSymbolicName());
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		List<String> bundleNames = new ArrayList<String>();
		for (Bundle bundle : bundles) {
			bundleNames.add(bundle.getSymbolicName());
			log.info("Bundle: " + bundle.getBundleContext());
		}
		return bundleNames;
	}

}
