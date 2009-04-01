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
package com.sun.jersey.impl.methodparams;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.impl.AbstractResourceTester;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class FormParamTest extends AbstractResourceTester {

    public FormParamTest(String testName) {
        super(testName);
    }
    
    @XmlRootElement
    public static class JAXBBean {

        public String value;
        
        public JAXBBean() {}

        public boolean equals(Object o) {
            if (!(o instanceof JAXBBean))
                return false;
            return ((JAXBBean) o).value.equals(value);
        }

        public String toString() {
            return "JAXBClass: "+value;
        }
    }

    @Path("/")
    public class FormResourceX {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String post(
                @FormParam("a") String a,
                @FormParam("b") String b,
                MultivaluedMap<String, String> form,
                @Context UriInfo ui,
                @QueryParam("a") String qa) {
            assertEquals(a, form.getFirst("a"));
            assertEquals(b, form.getFirst("b"));
            return a + b;
        }
    }

    @Path("/")
    public class FormResourceY {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String post(
                @FormParam("a") String a,
                @FormParam("b") String b,
                Form form,
                @Context UriInfo ui,
                @QueryParam("a") String qa) {
            assertEquals(a, form.getFirst("a"));
            assertEquals(b, form.getFirst("b"));
            return a + b;
        }
    }

    public void testFormParamX() {
        initiateWebApplication(FormResourceX.class);

        WebResource r = resource("/");

        Form form = new Form();
        form.add("a", "foo");
        form.add("b", "bar");

        String s = r.post(String.class, form);
        assertEquals("foobar", s);
    }

    public void testFormParamY() {
        initiateWebApplication(FormResourceY.class);

        WebResource r = resource("/");

        Form form = new Form();
        form.add("a", "foo");
        form.add("b", "bar");

        String s = r.post(String.class, form);
        assertEquals("foobar", s);
    }

    @Path("/")
    public class MultipartFormResourceX {
        @POST
        @Consumes({"multipart/form-data", MediaType.APPLICATION_FORM_URLENCODED})
        public String post(
                @FormParam("a") String a,
                @FormParam("b") String b,
                @FormParam("c") JAXBBean c,
                @FormParam("c") FormDataContentDisposition cdc,
                MimeMultipart m,
                @Context UriInfo ui,
                @QueryParam("a") String qa) throws Exception {
            assertEquals(3, m.getCount());
            return a + b + cdc.getFileName();
        }
    }

    @Path("/")
    public class MultipartFormResourceY {
        @POST
        @Consumes({"multipart/form-data", MediaType.APPLICATION_FORM_URLENCODED})
        public String post(
                @FormParam("a") String a,
                @FormParam("b") String b,
                @FormParam("c") JAXBBean c,
                @FormParam("c") FormDataContentDisposition cdc,
                Form form,
                @Context UriInfo ui,
                @QueryParam("a") String qa) throws Exception {
            assertEquals(a, form.getFirst("a"));
            assertEquals(b, form.getFirst("b"));
            assertNull(cdc);
            return a + b;
        }
    }

    public void testMultipartFormParam() throws Exception {
        initiateWebApplication(MultipartFormResourceX.class);

        WebResource r = resource("/");

        MimeMultipart form = new MimeMultipart();

        InternetHeaders headers = new InternetHeaders();
        headers.addHeader("content-disposition", "form-data; name=\"a\"");
        MimeBodyPart bp = new MimeBodyPart(headers, "foo".getBytes());
        form.addBodyPart(bp);

        headers = new InternetHeaders();
        headers.addHeader("content-disposition", "form-data; name=\"b\"");
        bp = new MimeBodyPart(headers, "bar".getBytes());
        form.addBodyPart(bp);

        headers = new InternetHeaders();
        headers.addHeader("content-disposition", "form-data; name=\"c\"; filename=\"file.xml\"");
        headers.addHeader("Content-type", "application/xml");
        bp = new MimeBodyPart(headers, "<jaxbBean><value>content</value></jaxbBean>".getBytes());
        form.addBodyPart(bp);

        String s = r.type("multipart/form-data").post(String.class, form);
        assertEquals("foobarfile.xml", s);
    }

    public void testMultipartFormParamWithForm() {
        initiateWebApplication(MultipartFormResourceY.class);

        WebResource r = resource("/");

        Form form = new Form();
        form.add("a", "foo");
        form.add("b", "bar");
        form.add("c", "<jaxbBean><value>content</value></jaxbBean>");

        String s = r.post(String.class, form);
        assertEquals("foobar", s);
    }


    @Path("/")
    public class MultipartFormParamTypes
    {
        @POST
        @Consumes("multipart/form-data")
        public String createSubscription(
                @FormParam("int") int i,
                @FormParam("float") float f,
                @FormParam("decimal") BigDecimal d
                ) {
            return "" + i + " " + f + " " + d;
        }
    }

    public void testMultipartFormListSubscription() throws Exception {
        initiateWebApplication(MultipartFormParamTypes.class);

        WebResource r = resource("/");

        MimeMultipart form = new MimeMultipart();

        InternetHeaders headers = new InternetHeaders();
        headers.addHeader("content-disposition", "form-data; name=\"int\"");
        MimeBodyPart bp = new MimeBodyPart(headers, "1234".getBytes());
        form.addBodyPart(bp);

        headers = new InternetHeaders();
        headers.addHeader("content-disposition", "form-data; name=\"float\"");
        bp = new MimeBodyPart(headers, "3.14".getBytes());
        form.addBodyPart(bp);

        headers = new InternetHeaders();
        headers.addHeader("content-disposition", "form-data; name=\"decimal\"");
        headers.addHeader("Content-type", "application/xml");
        bp = new MimeBodyPart(headers, "1.61".getBytes());
        form.addBodyPart(bp);

        String s = r.type("multipart/form-data").post(String.class, form);
        assertEquals("1234 3.14 1.61", s);
    }

    @Path("/")
    public class MultipartFormResourceNull {
        @POST
        @Consumes({"multipart/form-data", MediaType.APPLICATION_FORM_URLENCODED})
        public String post(
                @FormParam("a") String a,
                @FormParam("b") String b) throws Exception {
            assertNotNull(a);
            assertNull(b);
            return a;
        }
    }

    public void testMultipartFormParamWithFormNull() {
        initiateWebApplication(MultipartFormResourceNull.class);

        WebResource r = resource("/");

        Form form = new Form();
        form.add("a", "foo");

        String s = r.post(String.class, form);
        assertEquals("foo", s);
    }

    public void testMultipartFormParamWithFormDataNull() throws Exception {
        initiateWebApplication(MultipartFormResourceNull.class);

        WebResource r = resource("/");

        MimeMultipart form = new MimeMultipart();

        InternetHeaders headers = new InternetHeaders();
        headers.addHeader("content-disposition", "form-data; name=\"a\"");
        MimeBodyPart bp = new MimeBodyPart(headers, "foo".getBytes());
        form.addBodyPart(bp);

        String s = r.type("multipart/form-data").post(String.class, form);
        assertEquals("foo", s);
    }



    @Path("/")
    public class FormResourceJAXB {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public JAXBBean post(
                @FormParam("a") JAXBBean a,
                @FormParam("b") List<JAXBBean> b) {
            assertEquals("a", a.value);
            assertEquals(2, b.size());
            assertEquals("b1", b.get(0).value);
            assertEquals("b2", b.get(1).value);
            return a;
        }
    }

    public void testFormParamJAXB() {
        initiateWebApplication(FormResourceJAXB.class);

        WebResource r = resource("/");

        Form form = new Form();
        form.add("a", "<jaxbBean><value>a</value></jaxbBean>");
        form.add("b", "<jaxbBean><value>b1</value></jaxbBean>");
        form.add("b", "<jaxbBean><value>b2</value></jaxbBean>");

        JAXBBean b = r.post(JAXBBean.class, form);
        assertEquals("a", b.value);
    }

    @Path("/")
    public class FormResourceDate {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String post(
                @FormParam("a") Date a,
                @FormParam("b") Date b,
                @FormParam("c") Date c) {
            assertNotNull(a);
            assertNotNull(b);
            assertNotNull(c);
            return "POST";
        }
    }

    public void testFormParamDate() {
        initiateWebApplication(FormResourceDate.class);

        WebResource r = resource("/");

        String date_RFC1123 = "Sun, 06 Nov 1994 08:49:37 GMT";
        String date_RFC1036 = "Sunday, 06-Nov-94 08:49:37 GMT";
        String date_ANSI_C = "Sun Nov  6 08:49:37 1994";

        Form form = new Form();
        form.add("a", date_RFC1123);
        form.add("b", date_RFC1036);
        form.add("c", date_ANSI_C);

        String b = r.post(String.class, form);
        assertEquals("POST", b);
    }

}