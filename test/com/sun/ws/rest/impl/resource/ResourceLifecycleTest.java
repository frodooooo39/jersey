/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved. 
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License. 
 * 
 * You can obtain a copy of the License at:
 *     https://jersey.dev.java.net/license.txt
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each
 * file and include the License file at:
 *     https://jersey.dev.java.net/license.txt
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 *     "Portions Copyrighted [year] [name of copyright owner]"
 */

package com.sun.ws.rest.impl.resource;

import com.sun.ws.rest.impl.AbstractResourceTester;
import com.sun.ws.rest.api.core.ResourceConfig;
import com.sun.ws.rest.impl.application.WebApplicationImpl;
import com.sun.ws.rest.api.core.DefaultResourceConfig;
import com.sun.ws.rest.impl.client.ResourceProxy;
import com.sun.ws.rest.impl.resource.PerRequestProvider;
import com.sun.ws.rest.spi.resource.PerRequest;
import com.sun.ws.rest.spi.resource.Singleton;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author Marc Hadley
 */
public class ResourceLifecycleTest extends AbstractResourceTester {
    
    @Path("foo")
    @Singleton
    public static class TestFooBean {
        
        private int count;
        
        public TestFooBean() {
            this.count = 0;
        }
        
        @GET
        public String doGet() {
            count++;
            return Integer.toString(count);
        }
        
    }
    
    @Path("bar")
    @PerRequest
    public static class TestBarBean {
        
        private int count;
        
        public TestBarBean() {
            this.count = 0;
        }
        
        @GET
        public String doGet() {
            count++;
            return Integer.toString(count);
        }
        
    }
    
    @Path("baz")
    public static class TestBazBean {
        
        private int count;
        
        public TestBazBean() {
            this.count = 0;
        }
        
        @GET
        public String doGet() {
            count++;
            return Integer.toString(count);
        }
        
    }
    
    WebApplicationImpl a;
    
    public ResourceLifecycleTest(String testName) {
        super(testName);
    }
    
    private void initiate(ResourceConfig c) {
        a = new WebApplicationImpl();
        a.initiate(null, c);        
    }
    
    private ResourceConfig getResourceConfig() {
        final Set<Class> r = new HashSet<Class>();
        r.add(TestFooBean.class);
        r.add(TestBarBean.class);
        r.add(TestBazBean.class);
        return new DefaultResourceConfig(r);
    }
    
    public void testDefault() {
        initiateWebApplication(getResourceConfig());
        _test();
    }
    
    public void testOverrideDefault() {
        ResourceConfig c = getResourceConfig();
        c.getProperties().put(ResourceConfig.PROPERTY_DEFAULT_RESOURCE_PROVIDER_CLASS,
                PerRequestProvider.class);
        
        initiateWebApplication(c);
        _test();
    }
    
    public void testNullResourceProviderProperty() {
        ResourceConfig c = getResourceConfig();
        c.getProperties().put(ResourceConfig.PROPERTY_DEFAULT_RESOURCE_PROVIDER_CLASS,
                null);
        
        initiateWebApplication(c);
        _test();
    }
    
    public void testBadTypeResourceProviderProperty() {
        ResourceConfig c = getResourceConfig();
        c.getProperties().put(ResourceConfig.PROPERTY_DEFAULT_RESOURCE_PROVIDER_CLASS,
                "VALUE");

        boolean caught = false;
        try {
            initiateWebApplication(c);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
    }
    
    public void testBadClassResourceProviderProperty() {
        ResourceConfig c = getResourceConfig();
        c.getProperties().put(ResourceConfig.PROPERTY_DEFAULT_RESOURCE_PROVIDER_CLASS,
                String.class);
        
        boolean caught = false;
        try {
            initiateWebApplication(c);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);
    }
    
    private void _test() {
        ResourceProxy r = resourceProxy("/foo");        
        assertEquals("1", r.get(String.class));
        assertEquals("2", r.get(String.class));
        assertEquals("3", r.get(String.class));
        
        r = resourceProxy("/bar");        
        assertEquals("1", r.get(String.class));
        assertEquals("1", r.get(String.class));
        assertEquals("1", r.get(String.class));
        
        r = resourceProxy("/baz");        
        assertEquals("1", r.get(String.class));
        assertEquals("1", r.get(String.class));
        assertEquals("1", r.get(String.class));
    }
}