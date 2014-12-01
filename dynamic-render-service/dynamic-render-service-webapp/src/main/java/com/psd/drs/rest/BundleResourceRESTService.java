package com.psd.drs.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.psd.drs.service.BundleManager;
import com.psd.rendering.drs.osgi.api.RenderService;

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

//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<String> listAllBundles() {
//
//		return bundleManager.getAllBundles();
//	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response render() {
		RenderService renderService = bundleManager.getRenderService();
		String response = renderService.render("REST API call render.");
		return Response.ok(response).build();
	}

}
