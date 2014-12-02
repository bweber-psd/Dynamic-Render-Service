package com.psd.rendering.drs.osgi.service;

import com.psd.rendering.drs.osgi.api.RenderService;
import com.saperion.connector.render.engine.RenderEngine;
import com.saperion.connector.renditions.aspose.AsposeRenderEngine;


public class RenderServiceOOImpl implements RenderService {
    @Override
    public String render(String arg) {
    	System.out.println("Open Office Render Service received " + arg);
        return new StringBuilder(arg).reverse().toString();
    }

	@Override
	public RenderEngine getRenderEngine() {
		return new AsposeRenderEngine();
	}
}
