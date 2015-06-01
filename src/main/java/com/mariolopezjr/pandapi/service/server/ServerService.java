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

package com.mariolopezjr.pandapi.service.server;

import com.mariolopezjr.pandapi.data.server.Server;

import java.util.List;
import java.util.UUID;

/**
 * The Server service allows code to create, retrieve, and delete server resources.  All of the business logic
 * for these actions are here.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
public interface ServerService {

    /**
     * Retrieve a list of all of the server resources (including destroyed) currently in the system.
     * @return {@link List}<{@link Server}>
     */
    List<Server> getAllServers();

    /**
     * Retrieve a list of all of the server resources (including destroyed) currently in the system.  The list of
     * servers will be sorted by their {@link Server#id} field according to {@link java.util.UUID#compareTo(UUID)}.
     * @return  {@link List}<{@link Server}>
     */
    List<Server> getAllServersSortedById();

    /**
     * Retrieve the server resource with the specified ID
     * @param serverId {@link String} UUID string
     * @return {@link Server}
     */
    Server getServerById(String serverId);

    /**
     * Create a new server with the specified values.
     * @param server {@link Server} the requested values
     * @return {@link Server} the persisted server with a unique ID and a state
     */
    Server createServer(Server server);

    /**
     * Destroys the server with the specified ID.
     * @param serverId {@link String} UUID string
     * @throws com.mariolopezjr.pandapi.exception.ResourceNotFoundException if server does not exist
     */
    void deleteServer(String serverId);
}
