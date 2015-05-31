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
        try {
            Collection<Server> servers = dataStore.values();
            List<Server> clonedServers = new ArrayList<>(servers.size());

            // clone each instance to prevent client code from directly writing to the in-memory data store
            for (Server server : servers) {
                clonedServers.add(server.clone());
            }

            return Collections.unmodifiableList(clonedServers);
        } catch (CloneNotSupportedException e) {
            // should never happen
            throw new InternalException("Did someone remove the Server.clone() method!?!", e);
        }
    }
}
