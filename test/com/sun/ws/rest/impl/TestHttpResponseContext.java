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

package com.sun.ws.rest.impl;

import com.sun.ws.rest.spi.container.ContainerRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class TestHttpResponseContext extends HttpResponseContextImpl {

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    public TestHttpResponseContext(ContainerRequest requestContext) {
        super(requestContext);
    }
    
    protected OutputStream getUnderlyingOutputStream() throws IOException {
        return baos;
    }

    protected void commitStatusAndHeaders() throws IOException {
    }
    
    public byte[] getEntityAsByteArray() {
        return baos.toByteArray();
    }
}
