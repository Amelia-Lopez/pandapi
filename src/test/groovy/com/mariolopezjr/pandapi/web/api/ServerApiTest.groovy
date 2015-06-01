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

package com.mariolopezjr.pandapi.web.api

import com.mariolopezjr.pandapi.data.server.Server
import com.mariolopezjr.pandapi.data.server.ServerState
import com.mariolopezjr.pandapi.data.server.ServerUtility
import com.mariolopezjr.pandapi.exception.InternalException
import com.mariolopezjr.pandapi.service.server.ServerService
import com.mariolopezjr.pandapi.web.document.server.ServerDoc
import com.mariolopezjr.pandapi.web.document.server.ServerGetListResponse
import com.mariolopezjr.pandapi.web.document.server.ServerGetResponse
import com.mariolopezjr.pandapi.web.document.server.ServerPostRequest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.core.UriInfo

/**
 * Unit tests for the {@link ServerApi} class.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
class ServerApiTest extends Specification {

    @Shared
    private ServerApi codeUnderTest

    @Shared
    private ServerService serverService

    @Shared
    private UriInfo uriInfo

    @Shared
    private UriBuilder uriBuilder

    /**
     * Set some of the shared fields up once before any of the test runs.
     * @return
     */
    def setupSpec() {
        // stubs

        uriBuilder = Mock(UriBuilder)
        // because it returns itself, this has to be on a separate line and not in a closure
        uriBuilder.path(_ as String) >> uriBuilder

        uriInfo = Mock(UriInfo) { getAbsolutePathBuilder() >> uriBuilder }
    }

    /**
     * Set the shared fields up before each test.
     */
    def setup() {
        // mocks
        serverService = Mock(ServerService)

        codeUnderTest = new ServerApi(serverService)
    }

    @Unroll
    def "retrieve list of servers when we get back #instances instance(s) from the service"() {
        when: "the Api is called to get the list of all of the servers"
        ServerGetListResponse response = codeUnderTest.servers

        then: "the service was called once, and we get the same number of instances back"
        1 * serverService.allServers >> ServerUtility.generateServerInstances(instances)
        response
        response.servers.size() == instances

        where:
        instances | _
        0         | _
        1         | _
        2         | _
        10        | _
        100       | _
    }

    @Unroll
    def "create a server that will be in the '#state' state"() {
        given: "a valid request"
        def request = new ServerPostRequest(server: new ServerDoc(name: 'a', cpus: 1, ram: 2, diskSpace: 4))

        and: "the UriInfo has been set like it would have been by the context"
        codeUnderTest.uriInfo = uriInfo

        when: "the Api is called with the request"
        Response response = codeUnderTest.createServer(request)

        then: "the server returns the correct HTTP status code"
        notThrown(InternalException)
        1 * serverService.createServer(_ as Server) >> new Server(id: UUID.randomUUID(), state: state)
        response.status == status
        response.entity

        where:
        state                | status
        ServerState.BUILDING | Response.Status.ACCEPTED.statusCode
        ServerState.RUNNING  | Response.Status.CREATED.statusCode
    }

    def "create a server that will be in an unrecognized state"() {
        given: "a valid request"
        def request = new ServerPostRequest(server: new ServerDoc(name: 'a', cpus: 1, ram: 2, diskSpace: 4))

        and: "the created server's state is not valid for having just been created"
        ServerState state = ServerState.DESTROYED

        and: "the UriInfo has been set like it would have been by the context"
        codeUnderTest.uriInfo = uriInfo

        when: "the Api is called with the request"
        codeUnderTest.createServer(request)

        then: "the server returns the correct HTTP status code"
        thrown(InternalException)
        1 * serverService.createServer(_ as Server) >> new Server(id: UUID.randomUUID(), state: state)
    }

    def "retrieve server by id"() {
        given: "a valid ID"
        UUID id = UUID.randomUUID()

        and: "a valid server state"
        ServerState state = ServerState.BUILDING

        when: "the Api is called with the id"
        ServerGetResponse response = codeUnderTest.getServerById(id as String)

        then: "it returns successfully"
        notThrown(Exception)
        1 * serverService.getServerById(id.toString()) >> new Server(id: id, state: state)
        response.server
        response.server.id == id
    }

    def "delete a server by id"() {
        given: "a valid ID"
        String id = UUID.randomUUID().toString()

        when: "the Api is called with the id"
        Response response = codeUnderTest.deleteServer(id)

        then: "the server was called to delete the id and we get a 204 back"
        1 * serverService.deleteServer(id)
        response.status == Response.Status.NO_CONTENT.statusCode
    }
}
