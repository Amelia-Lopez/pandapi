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
import com.mariolopezjr.pandapi.service.server.ServerService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of the {@link ServerService} contract.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
public class ServerServiceImpl implements ServerService {

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

        Collections.sort(servers, SERVER_UUID_COMPARATOR);

        return servers;
    }
}
