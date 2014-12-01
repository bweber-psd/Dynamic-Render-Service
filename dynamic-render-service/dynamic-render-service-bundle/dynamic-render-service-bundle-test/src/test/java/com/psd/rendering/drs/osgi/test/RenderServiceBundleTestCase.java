package com.psd.rendering.drs.osgi.test;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.psd.rendering.drs.osgi.api.RenderService;

@RunWith(JUnit4TestRunner.class)
public class RenderServiceBundleTestCase {

    @Inject
    private BundleContext ctx;

    @Configuration
    public Option[] config() {
        return CoreOptions.options(
                CoreOptions.mavenBundle("com.psd.rendering.drs", "dynamic-render-service-bundle-api"),
                CoreOptions.mavenBundle("com.psd.rendering.drs", "dynamic-render-service-bundle-service"),
                CoreOptions.junitBundles());
    }

    @Test
    public void getHelloService() {
        ServiceReference ref = ctx.getServiceReference(RenderService.class.getName());
        RenderService svc = (RenderService) ctx.getService(ref);

        Assert.assertEquals("This service implementation should reverse the input",
                "4321", svc.render("1234"));
    }
}