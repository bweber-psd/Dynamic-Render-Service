package com.psd.rendering.drs.osgi.service;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.psd.rendering.drs.osgi.api.RenderService;

public class Activator implements BundleActivator {
    private ServiceRegistration reg;

    @Override
    public void start(BundleContext context) throws Exception {
        reg = context.registerService(RenderService.class.getName(), new RenderServiceImpl(), new Properties());
        System.out.println("Registration: " + reg.toString());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        reg.unregister();
    }
}
