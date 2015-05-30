/*
 * Copyright 2015 Mario Lopez Jr
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mariolopezjr.pandapi.web.application;

import com.mariolopezjr.pandapi.web.api.ServerApi;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Entry point of the application containing the main method.  This class does the minimum required to start the
 * REST API.
 * @author Mario Lopez Jr
 * @since 0.0.1
 */
public class PandapiWebApplication {

    // base context path for the web server
    private final static String CONTEXT_PATH = "/";  // todo: make this configurable

    // port that the web server should listen on
    private final static int PORT = 8080;  // todo: make this configurable

    /**
     * Entry point for the application.  Sets up an HTTP server on the configured port.
     * @param args {@link String[]} parameter is ignored
     * @throws Exception If Jetty fails to start or is interrupted.
     */
    public static void main(String[] args) throws Exception {
        // set up Jetty with our configured Jersey servlet
        Server server = setupJetty(createServlet());

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    /**
     * Creates and sets up a Jetty server with the specified {@link ServletHolder} servlet.
     * @param servletHolder {@link ServletHolder} The servlet to add to the Jetty servlet context
     * @return {@link Server} The Jetty server
     */
    private static Server setupJetty(final ServletHolder servletHolder) {
        // set up the Jetty context and add the specified servlet
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(CONTEXT_PATH);
        context.addServlet(servletHolder, "/*");

        // create the Jetty server and add the context
        Server jettyServer = new Server(PORT);
        jettyServer.setHandler(context);

        return jettyServer;
    }

    /**
     * Creates a Jersey servlet holder referencing our APIs.
     * @return {@link ServletHolder} The Jersey servlet
     */
    private static ServletHolder createServlet() {
        // use a ResourceConfig to specify which Java packages Jersey should scan
        ResourceConfig resourceConfig = new ResourceConfig() {{
            // specify the API package by using a concrete class to give us static type checking
            packages(ServerApi.class.getPackage().getName());
        }};

        return new ServletHolder(new ServletContainer(resourceConfig));
    }
}
