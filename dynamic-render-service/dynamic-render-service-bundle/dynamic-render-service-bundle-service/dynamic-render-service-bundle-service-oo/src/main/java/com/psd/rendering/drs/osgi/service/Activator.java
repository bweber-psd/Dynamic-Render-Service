package com.psd.rendering.drs.osgi.service;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.psd.rendering.drs.osgi.api.RenderService;

public class Activator implements BundleActivator {
    private ServiceRegistration reg;
    private OfficeManager officeManager;

    @Override
    public void start(BundleContext context) throws Exception {
    	
    	officeManager = new DefaultOfficeManagerConfiguration().setOfficeHome("C:\\Program Files (x86)\\OpenOffice.org 3").buildOfficeManager();
        officeManager.start();
        
        reg = context.registerService(RenderService.class.getName(), new RenderServiceOOImpl(officeManager), null);
        System.out.println("Registration: " + reg.toString());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    	
    	officeManager.stop();
        reg.unregister();
    }
}
