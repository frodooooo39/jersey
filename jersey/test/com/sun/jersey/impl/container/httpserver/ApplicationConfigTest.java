/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jersey.impl.container.httpserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.core.ApplicationConfig;
import javax.ws.rs.ext.RuntimeDelegate;
import junit.framework.*;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class ApplicationConfigTest extends AbstractHttpServerTester {
    @Path("/")
    public static class Resource {
        @GET
        public String get() {
            return "CONTENT";
        }
    }
        
    public ApplicationConfigTest(String testName) {
        super(testName);
    }
    
    public void testApplicationConfig() {
        ApplicationConfig ac = new ApplicationConfig() {
            @Override
            public Set<Class<?>> getResourceClasses() {
                Set<Class<?>> s = new HashSet<Class<?>>();
                s.add(Resource.class);
                return s;
            }            
        };
        
        HttpHandler hh = RuntimeDelegate.getInstance().
                createEndpoint(ac, HttpHandler.class);
        start(hh);
        
        WebResource r = Client.create().resource(getUri().build());
        assertEquals("CONTENT", r.get(String.class));
    }
}