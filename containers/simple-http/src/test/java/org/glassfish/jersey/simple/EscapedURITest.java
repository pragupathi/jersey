/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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
package org.glassfish.jersey.simple;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import static org.junit.Assert.assertEquals;

/**
 * @author Paul Sandoz (paul.sandoz at oracle.com)
 */
@Ignore("This needs to be enabled after fixing on the URI processing bug.")
public class EscapedURITest extends AbstractSimpleServerTester {
    @Path("x%20y")
    public static class EscapedURIResource {
        @GET
        public String get(@Context UriInfo info) {
            assertEquals(CONTEXT + "/x%20y", info.getAbsolutePath().getRawPath());
            assertEquals(CONTEXT + "/", info.getBaseUri().getRawPath());
            assertEquals("x y", info.getPath());
            assertEquals("x%20y", info.getPath(false));
            return "CONTENT";
        }
    }

    @Path("x y")
    public static class NonEscapedURIResource extends EscapedURIResource {
    }

    @Test
    public void testEscaped() {
        ResourceConfig config = new ResourceConfig(EscapedURIResource.class);
        startServer(config);
        Client client = ClientBuilder.newClient();
        WebTarget r = client.target(getUri().userInfo("x.y").path("x%20y").build());
        assertEquals("CONTENT", r.request().get(String.class));
    }

    @Test
    public void testNonEscaped() {
        startServer(NonEscapedURIResource.class);
        Client client = ClientBuilder.newClient();
        WebTarget r = client.target(getUri().userInfo("x.y").path("x%20y").build());
        assertEquals("CONTENT", r.request().get(String.class));
    }

}
