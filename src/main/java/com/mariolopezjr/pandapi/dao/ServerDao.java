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

package com.mariolopezjr.pandapi.dao;

import com.mariolopezjr.pandapi.data.server.Server;

import java.util.List;
import java.util.UUID;

/**
 * The Server DAO allows code to access the backing data store for server resources.
 * @author Mario Lopez Jr
 * @since 0.0.6
 */
public interface ServerDao {

    /**
     * Retrieve a list of all of the server resources (including destroyed) currently in the backing data store.
     * @return {@link List}<{@link Server}>
     */
    List<Server> getAllServers();

    /**
     * Retrieve the {@link Server} with the specified server identifier.
     * @param serverId {@link UUID} the server identifier
     * @return {@link Server}
     */
    Server getServerById(UUID serverId);

    /**
     * Persists the new server resource into the data store.  A {@link Server} is returned in case the data store
     * makes any updates to the resource (e.g. setting the identifier).
     * @param server {@link Server}
     * @return {@link Server}
     */
    Server createServer(Server server);

    /**
     * Updates the existing server resource with the same id as the specified server to its values (other than id).
     * @param server {@link Server} the server resource with the updated values to use
     * @return boolean true if a server resource was updated, false if nothing was updated (e.g. it didn't already exist)
     */
    boolean updateServer(Server server);

    /**
     * Deletes the server resource with the specified server identifier.  Returns the number of records affected by
     * this action.
     * @param serverId {@link UUID} the server identifier
     * @return boolean true if the server resource was deleted, false if there was nothing to delete (e.g. it didn't exist)
     */
    boolean deleteServer(UUID serverId);
}
