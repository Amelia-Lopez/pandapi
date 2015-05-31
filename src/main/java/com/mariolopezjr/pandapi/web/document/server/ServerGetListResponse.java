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

package com.mariolopezjr.pandapi.web.document.server;

import com.mariolopezjr.pandapi.data.server.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * The data model for the GET response for a list of resources.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
public class ServerGetListResponse {

    private List<ServerDoc> servers;

    /**
     * Creates a new instance of this document with the relevant values from the provided domain objects.
     * @param domainObjects {@link List}<{@link Server}>
     * @return {@link ServerGetListResponse}
     */
    public static ServerGetListResponse fromDomainObject(final List<Server> domainObjects) {
        ServerGetListResponse doc = new ServerGetListResponse();

        // populate the list of servers from the list of domain object servers
        List<ServerDoc> serverDocs = new ArrayList<>();
        for (Server domainObject : domainObjects) {
            serverDocs.add(ServerDoc.fromDomainObject(domainObject));
        }
        doc.setServers(serverDocs);

        return doc;
    }


    public List<ServerDoc> getServers() {
        return servers;
    }

    public void setServers(List<ServerDoc> servers) {
        this.servers = servers;
    }
}
