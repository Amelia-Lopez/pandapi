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

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Entry point of the application handling starting and stopping the server.
 * @author Mario Lopez Jr
 * @since 0.0.1
 */
public class PandapiWebApplication {

    // slf4j logger
    private static final Logger LOG = LoggerFactory.getLogger(PandapiWebApplication.class);

    // location of the main config file (that refers to our other config files)
    private static final String CONFIG_FILE = "config/config.xml";

    /**
     * Entry point for the application.  Sets up an HTTP server on the configured port.
     * @param args {@link String}[] parameter is ignored
     * @throws Exception If Jetty fails to start or is interrupted.
     */
    public static void main(String[] args) throws Exception {
        // configuration
        LOG.info("Loading configuration from file: {}", CONFIG_FILE);
        Configuration config = createConfiguration();

        startServer(config);
    }

    /**
     * Start the server.
     * @param config {@link Configuration}
     * @throws Exception
     */
    private static void startServer(final Configuration config) throws Exception {
        // we want to know when the server goes down
        logWhenServerShutsDown();

        // listen on the specified port for shutdown requests
        int port = config.getInt("application/shutdownPort");
        listenForShutdownRequest(port);

        // start the server
        PandapiRestServer restServer = new PandapiRestServer(config);
        restServer.start();
    }

    /**
     * Listen on the specified port for a shutdown request.  Right now any connection on that port will
     * result a server shutdown.
     * @param port int
     * @throws IOException
     */
    private static void listenForShutdownRequest(final int port) throws IOException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(port);
                    LOG.info("Listen for shutdown command on port {}", port);
                    server.accept();
                } catch (IOException e) {
                    LOG.error("Error listening on port {} for shutdown requests.", port, e);
                }

                LOG.info("Shutdown request received on port {}", port);
                System.exit(0);
            }
        };

        thread.start();
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
}
