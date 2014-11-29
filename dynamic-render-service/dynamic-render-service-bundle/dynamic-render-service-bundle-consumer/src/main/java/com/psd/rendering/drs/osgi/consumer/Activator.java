package com.psd.rendering.drs.osgi.consumer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.psd.rendering.drs.osgi.api.RenderService;


public class Activator implements BundleActivator {
    private ServiceTracker st;

    @Override
    public void start(BundleContext context) throws Exception {
        st = new ServiceTracker(context, RenderService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object svc = super.addingService(reference);
                if (svc instanceof RenderService) {
                    invokeService((RenderService) svc);
                }
                return svc;
            }
        };
        st.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        st.close();
    }

    void invokeService(RenderService svc) {
        String input = "Testing123";
        System.out.println("Invoking Service with input: " + input);
        System.out.println("  Result: " + svc.render(input));
    }
}
