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

package com.mariolopezjr.pandapi.web.api;

import com.mariolopezjr.pandapi.data.server.Server;
import com.mariolopezjr.pandapi.data.server.ServerState;
import com.mariolopezjr.pandapi.exception.InternalException;
import com.mariolopezjr.pandapi.service.server.ServerService;
import com.mariolopezjr.pandapi.web.document.server.ServerGetListResponse;
import com.mariolopezjr.pandapi.web.document.server.ServerGetResponse;
import com.mariolopezjr.pandapi.web.document.server.ServerPostRequest;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

/**
 * API code for the servers resource.  This class handles calling the server service to get domain objects for the
 * available data, creating a new document with the domain object data, and returning the document.  All API routing
 * for the endpoint is done here (i.e. path, methods, content type, etc.).
 * @author Mario Lopez Jr
 * @since 0.0.1
 */
@Service
@Path("/v1/servers")
public class ServerApi {

    // server service to handle the business logic for retrieving, creating, and deleting server resources
    private final ServerService serverService;

    // URI info of the client request
    @Context
    UriInfo uriInfo;

    /**
     * Constructor. Except in unit tests, this should never be called directly. Instead, use injection.
     * @param serverService {@link ServerService}
     */
    @Inject
    public ServerApi(final ServerService serverService) {
        this.serverService = serverService;
    }

    /**
     * Returns the list of server resources in the system.
     * @return {@link ServerGetListResponse}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ServerGetListResponse getServers() {
        return ServerGetListResponse.fromDomainObject(serverService.getAllServers());
    }

    /**
     * Creates a new server resource and returns the persisted resource with its new state and ID.
     * @param request {@link ServerPostRequest} the request from the client
     * @return {@link Response} wrapping a {@link ServerGetResponse} entity
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createServer(ServerPostRequest request) {
        Server createdServer = serverService.createServer(request.toDomainObject());
        ServerGetResponse response = ServerGetResponse.fromDomainObject(createdServer);

        // generate the URI for the new resource
        URI uri = uriInfo.getAbsolutePathBuilder().path(createdServer.getId().toString()).build();

        if (ServerState.BUILDING.equals(createdServer.getState())) {
            // servers take some time to build, so this is the expected path 100% of the time
            return Response.accepted(response).location(uri).build();
        } else if (ServerState.RUNNING.equals(createdServer.getState())) {
            // in case we get some magical server that builds instantly
            return Response.created(uri).entity(response).build();
        } else {
            // in case of Spanish Inquisition
            throw new InternalException("Server resource in unexpected state: " + createdServer);
        }
    }

    /**
     * Returns the specific server resource if it exists in the system.
     * @param serverId {@link String}
     * @return {@link ServerGetResponse}
     */
    @GET
    @Path("{serverId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ServerGetResponse getServerById(@PathParam("serverId") String serverId) {
        return ServerGetResponse.fromDomainObject(serverService.getServerById(serverId));
    }

    /**
     * Deletes the specific server resource if it exists in the system.  The server resource will have a state
     * of Destroyed until it is purged.
     * @param serverId {@link String}
     * @return {@link Response}
     */
    @DELETE
    @Path("{serverId}")
    public Response deleteServer(@PathParam("serverId") String serverId) {
        serverService.deleteServer(serverId);

        return Response.noContent().build();
    }
}
