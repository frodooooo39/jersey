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
package com.sun.jersey.spring;

import org.testng.annotations.Test;


/**
 * Test prototype resources that are managed by spring.<br>
 * Created on: Apr 10, 2008<br>
 * 
 * @author <a href="mailto:martin.grotzke@freiheit.com">Martin Grotzke</a>
 * @version $Id$
 */
@Test
public class SpringManagedPerRequestResourceTest extends SpringManagedPerRequestResourceTestBase {
    
    public SpringManagedPerRequestResourceTest() {
        super( "managedperrequest" );
    }

    /* (non-Javadoc)
     * @see com.sun.ws.rest.spring.SpringManagedPerRequestResourceTestBase#testGetAndUpdateCount()
     */
    @Override
    public void testGetAndUpdateCount() {
        super.testGetAndUpdateCount();
    }

    /* (non-Javadoc)
     * @see com.sun.ws.rest.spring.SpringManagedPerRequestResourceTestBase#testGetAndUpdateManagedPrototypeItem()
     */
    @Override
    public void testGetAndUpdateManagedPrototypeItem() {
        super.testGetAndUpdateManagedPrototypeItem();
    }

    /* (non-Javadoc)
     * @see com.sun.ws.rest.spring.SpringManagedPerRequestResourceTestBase#testGetAndUpdateManagedSingletonItem()
     */
    @Override
    public void testGetAndUpdateManagedSingletonItem() {
        super.testGetAndUpdateManagedSingletonItem();
    }

}