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

package com.sun.ws.rest.impl.bean;

import com.sun.ws.rest.impl.client.ResourceProxy;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.UriTemplate;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class DryHttpMethodsTest extends AbstractResourceTester {
    
    public DryHttpMethodsTest(String testName) {
        super(testName);
    }

    @UriTemplate("/")
    static public class Resource { 
        @HttpMethod
        public void headMe() {
        }
        
        @HttpMethod
        public String getMe() {
            return "getMe";
        }
        
        @HttpMethod
        public String putMe(String s) {
            assertEquals("putMe", s);
            return "putMe";
        }

        @HttpMethod
        public String postMe(String s) {
            assertEquals("postMe", s);
            return "postMe";
        }
        
        @HttpMethod
        public String deleteMe() {
            return "deleteMe";
        }
    }
    
    public void testMethod() {
        initiateWebApplication(Resource.class);
        ResourceProxy r = resourceProxy("/");
        
        r.head();
        assertEquals("getMe", r.get(String.class));
        assertEquals("putMe", r.put(String.class, "putMe"));
        assertEquals("postMe", r.post(String.class, "postMe"));
        assertEquals("deleteMe", r.delete(String.class));
    }
}
