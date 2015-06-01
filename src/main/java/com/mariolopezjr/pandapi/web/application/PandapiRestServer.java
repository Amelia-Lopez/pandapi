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
import org.apache.commons.configuration.Configuration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Panda API REST server.
 * @author Mario Lopez Jr
 * @since 0.2.0
 */
public class PandapiRestServer {

    // slf4j logger
    private static final Logger LOG = LoggerFactory.getLogger(PandapiRestServer.class);

    // base path for all of the config values for this class
    private static final String CONFIG_BASE_PATH = "webServer/";

    private final Configuration config;

    /**
     * Constructor
     * @param config {@link Configuration}
     */
    public PandapiRestServer(final Configuration config) {
        this.config = config;
    }

    public void start() throws Exception {
        // we want to keep track of when the server was started
        LOG.info("Starting Panda API server");

        // hack to both have the configuration here and also make it available through injection
        AbstractBinder configBinder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(config).to(Configuration.class);
            }
        };

        // dependency injection binders
        List<AbstractBinder> binders = Arrays.asList(
                new ServerServiceBinder(),
                new DaoBinder(),
                configBinder);

        // set up Jersey servlet
        ServletHolder servletHolder = createServlet(binders);

        // set up Jetty
        Server server = setupJetty(servletHolder);

        // main application loop
        server.start();
        server.join();
    }

    /**
     * Creates and sets up a Jetty server with the specified {@link ServletHolder} servlet.
     * @param servletHolder {@link ServletHolder} The servlet to add to the Jetty servlet context.
     * @return {@link Server} The Jetty server
     */
    public Server setupJetty(final ServletHolder servletHolder) {
        // configuration values
        String contextPath = config.getString(CONFIG_BASE_PATH + "contextPath");
        int port = config.getInt(CONFIG_BASE_PATH + "port");
        int clientTimeout = config.getInt(CONFIG_BASE_PATH + "clientTimeout");
        int maxThreads = config.getInt(CONFIG_BASE_PATH + "threadPool/maxThreads");
        int minThreads = config.getInt(CONFIG_BASE_PATH + "threadPool/minThreads");
        int idleTimeout = config.getInt(CONFIG_BASE_PATH + "threadPool/idleTimeout");

        LOG.debug("Listening on port {} with context path: {}", port, contextPath);
        LOG.debug("Using configuration values: clientTimeout={}, maxThreads={}, minThreads={}, idleTimeout={}",
                clientTimeout, maxThreads, minThreads, idleTimeout);

        // thread pool
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

        // create the Jetty server
        Server jettyServer = new Server(threadPool);

        // set port and client timeout
        ServerConnector connector = new ServerConnector(jettyServer);
        connector.setPort(port);
        connector.setIdleTimeout(clientTimeout);
        jettyServer.addConnector(connector);

        // set up the Jetty context and add the specified servlet
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(contextPath);
        context.addServlet(servletHolder, "/*");
        jettyServer.setHandler(context);

        // scheduler
        jettyServer.addBean(new ScheduledExecutorScheduler());

        // gracefully shut down Jetty when the JVM goes down
        jettyServer.setStopAtShutdown(true);

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

}
