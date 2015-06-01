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

import com.mariolopezjr.pandapi.dao.impl.DaoBinder;
import com.mariolopezjr.pandapi.service.server.impl.ServerServiceBinder;
import com.mariolopezjr.pandapi.web.api.ServerApi;
import com.mariolopezjr.pandapi.web.providers.InternalExceptionMapper;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Entry point of the application containing the main method.
 * @author Mario Lopez Jr
 * @since 0.0.1
 */
public class PandapiWebApplication {

    // slf4j logger
    private static final Logger LOG = LoggerFactory.getLogger(PandapiWebApplication.class);

    // location of the main config file (that refers to our other config files)
    private static final String CONFIG_FILE = "config/config.xml";

    // locations of fields in the config
    private static final String CONFIG_PORT = "webServer/port";
    private static final String CONFIG_CONTEXT_PATH = "webServer/contextPath";

    /**
     * Entry point for the application.  Sets up an HTTP server on the configured port.
     * @param args {@link String}[] parameter is ignored
     * @throws Exception If Jetty fails to start or is interrupted.
     */
    public static void main(String[] args) throws Exception {
        // configuration
        LOG.info("Loading configuration from file: {}", CONFIG_FILE);
        Configuration config = createConfiguration();

        // start the server
        start(config);
    }

    /**
     * Starts the API server.
     * todo: move this out into another class
     */
    private static void start(final Configuration config) throws Exception {
        // we want to keep track of when the server was started
        LOG.info("Starting Panda API server");

        // configured values
        int port = config.getInt(CONFIG_PORT);
        String contextPath = config.getString(CONFIG_CONTEXT_PATH);
        LOG.debug("Listening on port {} with context path: {}", port, contextPath);

        // dependency injection binders
        List<AbstractBinder> binders = Arrays.asList(
                new ServerServiceBinder(),
                new DaoBinder(),
                new ConfigBinder(config));

        // set up Jersey servlet
        ServletHolder servletHolder = createServlet(binders);

        // set up Jetty
        Server server = setupJetty(servletHolder, port, contextPath);

        // add a shutdown hook to log when the server is shut down
        logWhenServerShutsDown();

        try {
            // main application loop
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    /**
     * Creates and sets up a Jetty server with the specified {@link ServletHolder} servlet.
     * @param servletHolder {@link ServletHolder} The servlet to add to the Jetty servlet context.
     * @param port int the port that Jetty should listen on to serve HTTP requests
     * @param contextPath String the base context path of the server, e.g. /
     * @return {@link Server} The Jetty server
     */
    private static Server setupJetty(final ServletHolder servletHolder, int port, String contextPath) {
        // set up the Jetty context and add the specified servlet
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(contextPath);
        context.addServlet(servletHolder, "/*");

        // create the Jetty server and add the context
        Server jettyServer = new Server(port);
        jettyServer.setHandler(context);

        return jettyServer;
    }

    /**
     * Creates a Jersey servlet holder referencing our APIs.
     * @param binders {@link List}<{@link AbstractBinder}> list of binders that should be registered with Jersey
     * @return {@link ServletHolder} The Jersey servlet
     */
    private static ServletHolder createServlet(final List<AbstractBinder> binders) {
        // use a ResourceConfig to specify which Java packages Jersey should scan
        ResourceConfig resourceConfig = new ResourceConfig() {{
            // register our dependency injection bindings; services each get their own, DAOs share a single one
            for (AbstractBinder binder : binders) {
                register(binder);
            }

            // specify the API package by using a concrete class to give us some static type checking
            packages(ServerApi.class.getPackage().getName());

            // specify the exception mappers package by using a concrete class to give us some static type checking
            packages(InternalExceptionMapper.class.getPackage().getName());
        }};

        return new ServletHolder(new ServletContainer(resourceConfig));
    }

    /**
     * We want to know when the server shuts down, so add a shutdown hook to log it.
     */
    private static void logWhenServerShutsDown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Stopping Panda API server");
            }
        });
    }

    /**
     * Set up configuration.  I would prefer not to do this here, but I need access to the configuration before I'm
     * in code that gets its dependencies injected by Jersey/HK2.
     */
    private static Configuration createConfiguration() {
        CombinedConfiguration config = null;

        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(CONFIG_FILE);
            config = builder.getConfiguration(true);
            config.setExpressionEngine(new XPathExpressionEngine());
        } catch (Throwable t) {
            // don't bother running the application if we can't load configuration
            LOG.error("Unable to load configuration.", t);
            System.exit(1);
        }

        return config;
    }

    /**
     * Painful way of having our cake (using configuration in this class) and eating it, too (making configuration
     * available through dependency injection).
     */
    private static class ConfigBinder extends AbstractBinder {
        private final Configuration config;

        /**
         * Constructor to set the Configuration instane.
         * @param config {@link Configuration}
         */
        public ConfigBinder(final Configuration config) {
            this.config = config;
        }

        /**
         * Configure injection binding definitions
         */
        @Override
        protected void configure() {
            // binding an instance will make it a singleton
            bind(this.config).to(Configuration.class);
        }
    }
}
