package com.psd.rendering.drs.osgi.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.psd.rendering.drs.osgi.api.RenderService;


public class Activator implements BundleActivator {
    private ServiceTracker st;

    private static List<RenderService> serviceList = Collections.synchronizedList(new ArrayList<RenderService>());
    @Override
    public void start(BundleContext context) throws Exception {
        st = new ServiceTracker(context, RenderService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object svc = super.addingService(reference);
                if (svc instanceof RenderService) {
                    invokeService((RenderService) svc);
                    serviceList.add((RenderService) svc);
                }
                return svc;
            }
            @Override
            public void removedService(ServiceReference reference, Object service) {
            	super.removedService(reference, service);
            	if (service instanceof RenderService) {
            		serviceList.remove(service);
            	}
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
