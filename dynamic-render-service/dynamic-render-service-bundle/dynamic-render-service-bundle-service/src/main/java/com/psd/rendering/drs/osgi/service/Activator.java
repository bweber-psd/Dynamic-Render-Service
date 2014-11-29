package com.psd.rendering.drs.osgi.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.psd.rendering.drs.osgi.api.RenderService;

public class Activator implements BundleActivator {
    private ServiceRegistration reg;

    @Override
    public void start(BundleContext context) throws Exception {
        reg = context.registerService(RenderService.class.getName(), new RenderServiceImpl(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        reg.unregister();
    }
}
