package com.psd.rendering.drs.osgi.service;

import com.psd.rendering.drs.osgi.api.RenderService;
import com.saperion.connector.render.engine.RenderEngine;
import com.saperion.connector.renditions.aspose.AsposeRenderEngine;


public class RenderServiceImpl implements RenderService {
    @Override
    public String render(String arg) {
        return new StringBuilder(arg).reverse().toString();
    }

	@Override
	public RenderEngine getRenderEngine() {
		return new AsposeRenderEngine();
	}
}
