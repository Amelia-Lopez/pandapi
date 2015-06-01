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

package com.mariolopezjr.pandapi.dao.impl;

import com.mariolopezjr.pandapi.dao.ServerDao;
import com.mariolopezjr.pandapi.data.server.Server;
import com.mariolopezjr.pandapi.exception.InternalException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of the {@link ServerDao} interface that uses an internal in-memory data store.  The in-memory
 * data store will be backed by a {@link ConcurrentHashMap}.
 * @author Mario Lopez Jr
 * @since 0.0.6
 */
public class ServerInMemoryDao implements ServerDao {

    private final ConcurrentMap<UUID, Server> dataStore;

    /**
     * Constructor
     */
    public ServerInMemoryDao() {
        dataStore = createDataStore();
    }

    /**
     * Constructor for the unit test to call.  Purposely has package level scope.
     * @param dataStore {@link ConcurrentMap}<{@link UUID}, {@link Server}>
     */
    ServerInMemoryDao(final ConcurrentMap<UUID, Server> dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Creates an instance of a {@link ConcurrentHashMap}.  This will serve as the initial implementation of our
     * in-memory data store for the server resource.<br/>
     *
     * With an initial capacity of 1,024 and a load factor of 0.75f, we won't see any map re-sizing until we have
     * about 768 elements.  See {@link ConcurrentHashMap#ConcurrentHashMap(int, float, int)} for additional details.<br/>
     *
     * todo: These performance impacting factors should be configurable.
     * @return {@link ConcurrentMap}<{@link UUID}, {@link Server}>
     */
    private ConcurrentMap<UUID, Server> createDataStore() {
        // initial size of the ConcurrentHashMap to prevent frequent re-sizing below 768 entries
        int initialCapacity = 1_024;

        // the load factor, affects how often re-sizing occurs
        float loadFactory = 0.75f;

        // expected number of threads that will be updating the map at the same time
        int concurrencyLevel = 100;

        return new ConcurrentHashMap<>(initialCapacity, loadFactory, concurrencyLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Server> getAllServers() {
        Collection<Server> servers = dataStore.values();
        List<Server> clonedServers = new ArrayList<>(servers.size());

        // prevent the calling class from being able to manipulate the object in the data store directly
        for (Server server : servers) {
            clonedServers.add(server.clone());
        }

        return Collections.unmodifiableList(clonedServers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Server getServerById(UUID serverId) {
        Server result = dataStore.get(serverId);

        if (result != null) {
            // prevent the calling class from being able to manipulate the object in the data store directly
            result = result.clone();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Server createServer(Server server) {
        if (server.getId() != null) {
            // only the service should be calling the DAO directly, but in case someone else tries...
            throw new InternalException(
                    "New server resources get an ID from the DAO, but this server came in with an ID already: " + server);
        }

        // prevent the calling class from being able to manipulate the object in the data store directly
        Server clonedServer = server.clone();
        boolean serverCreated = false;

        while (!serverCreated) {
            UUID id = UUID.randomUUID();
            clonedServer.setId(id);

            // putIfAbsent will return null if the server was successfully persisted or it'll return the existing
            // server if the UUID was already in use.  In theory it should never happen.  In theory...
            Server existingServer = dataStore.putIfAbsent(id, clonedServer);

            if (null == existingServer) {
                serverCreated = true;
            }
        }

        // prevent the calling class from being able to manipulate the object in the data store directly
        clonedServer = clonedServer.clone();

        return clonedServer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateServer(Server server) {
        // prevent the calling class from being able to manipulate the object in the data store directly
        Server clonedServer = server.clone();

        // the resource will only be updated if the resource already existed
        Server previousValue = dataStore.replace(clonedServer.getId(), clonedServer);

        // return false if there was no previous value (which means nothing was updated)
        return previousValue != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteServer(UUID serverId) {
        // get the existing value to remove it from the data store safely
        Server server = dataStore.get(serverId);

        if (null == server) {
            // nothing to delete
            return false;
        }

        // delete the server
        return dataStore.remove(serverId, server);

    }
}
