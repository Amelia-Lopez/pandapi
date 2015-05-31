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
}
