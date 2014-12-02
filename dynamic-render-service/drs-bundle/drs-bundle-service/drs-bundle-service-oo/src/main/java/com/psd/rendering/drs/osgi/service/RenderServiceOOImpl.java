package com.psd.rendering.drs.osgi.service;

import org.artofsolving.jodconverter.office.OfficeManager;

import com.psd.rendering.drs.osgi.api.RenderService;
import com.psd.rendering.drs.osgi.service.engine.OORenderEngine;
import com.saperion.connector.render.engine.RenderEngine;
import com.saperion.connector.renditions.aspose.AsposeRenderEngine;


public class RenderServiceOOImpl implements RenderService {
    
	OfficeManager officeManager;
	
	public RenderServiceOOImpl(OfficeManager officeManager) {
		this.officeManager = officeManager;
	}

	@Override
    public String render(String arg) {
    	System.out.println("Open Office Render Service received " + arg);
        return new StringBuilder(arg).reverse().toString();
    }

	@Override
	public RenderEngine getRenderEngine() {
		return new OORenderEngine(officeManager);
	}
}
