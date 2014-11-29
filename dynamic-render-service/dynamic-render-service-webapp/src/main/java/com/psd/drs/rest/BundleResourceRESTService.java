package com.psd.drs.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.psd.drs.service.BundleManager;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to list all OSGi Bundles.
 */
@Path("/bundles")
@RequestScoped
public class BundleResourceRESTService {

	@Inject
	BundleManager bundleManager;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> listAllBundles() {

		return bundleManager.getAllBundles();
	}

}
