package com.psd.rendering.drs.osgi.consumer;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.psd.rendering.drs.osgi.api.RenderService;
import com.psd.rendering.drs.osgi.api.RenderServiceTracker;


public class Activator implements BundleActivator {
    private ServiceTracker st;
    private ServiceRegistration reg;
	private RenderServiceTracker renderServiceTracker;
    
//    private static List<RenderService> serviceList = Collections.synchronizedList(new ArrayList<RenderService>());
    @Override
    public void start(BundleContext context) throws Exception {
    	renderServiceTracker = new RenderServiceTrackerImpl();
    	reg = context.registerService(RenderServiceTracker.class.getName(), new RenderServiceTrackerImpl(), null);
        System.out.println("Registration: " + reg.toString());
        
        st = new ServiceTracker(context, RenderService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object svc = super.addingService(reference);
                if (svc instanceof RenderService) {
                    invokeService((RenderService) svc);
                    renderServiceTracker.addService((RenderService) svc);
                }
                return svc;
            }
            @Override
            public void removedService(ServiceReference reference, Object service) {
            	super.removedService(reference, service);
            	if (service instanceof RenderService) {
            		 renderServiceTracker.removeService((RenderService) service);
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
