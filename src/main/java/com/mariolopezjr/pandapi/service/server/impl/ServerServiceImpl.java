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

package com.mariolopezjr.pandapi.service.server.impl;

import com.mariolopezjr.pandapi.dao.ServerDao;
import com.mariolopezjr.pandapi.data.server.Server;
import com.mariolopezjr.pandapi.data.server.ServerState;
import com.mariolopezjr.pandapi.exception.BadRequestException;
import com.mariolopezjr.pandapi.exception.ResourceNotFoundException;
import com.mariolopezjr.pandapi.service.server.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link ServerService} contract.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
public class ServerServiceImpl implements ServerService {

    // slf4j logger
    private static final Logger LOG = LoggerFactory.getLogger(ServerServiceImpl.class);

    // comparator so we can sort Server instances by their id field
    private static final Comparator<Server> SERVER_UUID_COMPARATOR = new ServerUUIDComparator();

    // server DAO to give us the data
    private final ServerDao serverDao;

    /**
     * Constructor. Except in unit tests, this should never be called directly. Instead, use injection.
     * @param serverDao {@link ServerDao}
     */
    @Inject
    public ServerServiceImpl(final ServerDao serverDao) {
        this.serverDao = serverDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Server> getAllServers() {
        return serverDao.getAllServers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Server> getAllServersSortedById() {
        List<Server> servers = getAllServers();

        // sort the list of servers by id
        Collections.sort(servers, SERVER_UUID_COMPARATOR);

        return servers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Server getServerById(String serverId) {
        UUID id;

        try {
            id = UUID.fromString(serverId);
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException("Invalid server identifier: " + serverId);
        }

        Server server = serverDao.getServerById(id);

        if (null == server) {
            throw new ResourceNotFoundException("Server not found with identifier: " + serverId);
        }

        return server;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Server createServer(Server server) {
        // validate the request (this will throw an exception if the request is invalid)
        server.validateAsCreateRequest();

        // servers take time to come up, so set the state to BUILDING
        server.setState(ServerState.BUILDING);

        // persist the server resource in the data store (which will set the id)
        server = serverDao.createServer(server);

        LOG.info("Creating server: {}", server);

        // launch the actual server
        launchServer(server);

        return server;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteServer(String serverId) {
        Server server = getServerById(serverId);

        // we can only destroy servers that are currently running
        if (!ServerState.RUNNING.equals(server.getState())) {
            throw new BadRequestException("Only servers in the running state can be destroyed");
        }

        // servers take time to go down, so set the state to TERMINATING
        server.setState(ServerState.TERMINATING);

        // persist the server state update to the data store
        serverDao.updateServer(server);

        LOG.info("Destroying server: {}", server);

        // destroy the actual server
        destroyServer(server);
    }

    /**
     * Launch the server (or in this case, simulate it).
     * @param server {@link Server}
     */
    private void launchServer(Server server) {
        final Server clonedServer = server.clone();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // simulate the server taking 35 seconds to build
                    sleep(35_000);
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    // update server to running
                    clonedServer.setState(ServerState.RUNNING);
                    serverDao.updateServer(clonedServer);
                }
            }
        };

        thread.start();
    }

    /**
     * Destroy the server (or in this case, simulate it).
     * @param server {@link Server}
     */
    private void destroyServer(Server server) {
        final Server clonedServer = server.clone();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // simulate the server taking 30 seconds to go down
                    sleep(30_000);

                    // update server to destroyed
                    clonedServer.setState(ServerState.DESTROYED);
                    serverDao.updateServer(clonedServer);

                    /**
                     * Note: A non-simulated purge shouldn't take up a thread per resource nor be particularly concerned
                     * about specifically waiting 30 seconds to do the purge.  A real purge handler should use just one
                     * thread and occasionally wake up to see which server resources have been in the destroyed state
                     * for a long enough period of time (configurable, of course) and purge them.
                     */
                    // simulate a purge timer waiting 30 seconds
                    sleep(30_000);
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    // purge the server from the data store
                    serverDao.deleteServer(clonedServer.getId());
                }
            }
        };

        thread.start();
    }
}
