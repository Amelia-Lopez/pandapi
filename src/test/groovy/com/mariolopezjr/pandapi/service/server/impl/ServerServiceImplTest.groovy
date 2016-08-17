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

package com.mariolopezjr.pandapi.service.server.impl

import com.mariolopezjr.pandapi.dao.ServerDao
import com.mariolopezjr.pandapi.data.server.Server
import com.mariolopezjr.pandapi.data.server.ServerState
import com.mariolopezjr.pandapi.data.server.ServerUtility
import com.mariolopezjr.pandapi.exception.BadRequestException
import com.mariolopezjr.pandapi.exception.ResourceNotFoundException
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit tests for the {@link ServerServiceImpl} class.
 * @author Mario Lopez Jr
 * @since 0.0.6
 */
class ServerServiceImplTest extends Specification {

    @Shared
    private ServerServiceImpl codeUnderTest

    @Shared
    private ServerDao serverDao

    /**
     * Set up the shared fields before each test.
     */
    def setup() {
        // mocks
        serverDao = Mock(ServerDao)

        codeUnderTest = new ServerServiceImpl(serverDao)
    }

    @Unroll
    def "retrieve list of servers when there are #instances instance(s) in the data store"() {
        when: "the service is called to get the list of all of the servers"
        List<Server> response = codeUnderTest.allServers

        then: "the DAO was called once, and we get the same number of instances back"
        1 * serverDao.allServers >> ServerUtility.generateServerInstances(instances)
        response != null
        response.size() == instances

        where:
        instances | _
        0         | _
        1         | _
        2         | _
        10        | _
        100       | _
    }

    def "retrieve list of servers sorted by id"() {
        given: "there are 10 servers in the data store"
        int numOfInstances = 10
        List<Server> data = ServerUtility.generateUnsortedServerInstances(numOfInstances)
        List<Server> originalData = data.clone() as List<Server>  // array will be sorted in place, so clone it for later

        when: "the service is called to get the sorted list of all of the servers"
        List<Server> response = codeUnderTest.allServersSortedById

        then: "the DAO was called once, and the unsorted data is returned sorted by id"
        1 * serverDao.allServers >> data
        response != originalData
        response == originalData.sort()
    }

    def "retrieve server by valid id successfully"() {
        given: "a valid id"
        UUID id = UUID.randomUUID()

        when: "the service is called to retrieve the server"
        Server response = codeUnderTest.getServerById(id.toString())

        then: "we get the server successfully"
        notThrown(Exception)
        1 * serverDao.getServerById(id) >> new Server()
        response
    }

    def "retrieve server by malformed id unsuccessfully"() {
        given: "a malformed id"
        String id = "invalid_id"

        when: "the service is called to retrieve the server"
        codeUnderTest.getServerById(id)

        then: "it fails"
        thrown(BadRequestException)
    }

    def "retrieve server by non-existent valid id unsuccessfully"() {
        given: "a non-existent valid id"
        UUID id = UUID.randomUUID()

        when: "the service is called to retrieve the server"
        codeUnderTest.getServerById(id.toString())

        then: "it fails"
        thrown(ResourceNotFoundException)
        1 * serverDao.getServerById(id)
    }

    /**
     * Test: Create a new server using the service.
     * There are other things going on in the createServer method, but they happen over time.  It would take way too
     * long to test it in a unit test, so let the functional tests verify that behavior.
     */
    def "create a new server"() {
        given: "a valid request server"
        Server request = new Server(name: 'valid1', cpus: 1, ram: 1, diskSpace: 1)

        when: "the service is called to create the server"
        Server response = codeUnderTest.createServer(request)

        then: "the new server as a state of BUILDING"
        response.state == ServerState.BUILDING
        1 * serverDao.createServer(request) >> request
        response == request
    }

    /**
     * Test: Delete an existing server using the service.
     * There are other things going on in the deleteServer method, but they happen over time.  It would take way too
     * long to test it in a unit test, so let the functional tests verify that behavior.
     */
    def "delete a server"() {
        given: "a valid id"
        UUID id = UUID.randomUUID()

        and: "a valid server"
        Server existingServer = new Server(id: id, state: ServerState.RUNNING, name: 'a', cpus: 1, ram: 1, diskSpace: 1)
        Server updatedServer = existingServer.clone()
        updatedServer.state = ServerState.TERMINATING

        when: "the service is called to delete the server"
        codeUnderTest.deleteServer(id.toString())

        then: "the server is queried and updated to a terminating state"
        1 * serverDao.getServerById(id) >> existingServer
        1 * serverDao.updateServer(updatedServer)
    }

    @Unroll
    def "delete a server in the '#state' state unsuccessfully"() {
        given: "a valid id"
        UUID id = UUID.randomUUID()

        and: "a server in an invalid state for deletion"
        Server server = new Server(id: id, state: state)

        when: "the service is called to delete the server"
        codeUnderTest.deleteServer(id.toString())

        then: "it fails with a bad request"
        thrown(BadRequestException)
        1 * serverDao.getServerById(id) >> server

        where:
        state                   | _
        ServerState.BUILDING    | _
        ServerState.TERMINATING | _
        ServerState.DESTROYED   | _
    }

    def "delete a server using a malformed id unsuccessfully"() {
        given: "a malformed id"
        String id = "invalid_id"

        when: "the service is called to delete the server"
        codeUnderTest.deleteServer(id)

        then: "it fails"
        thrown(BadRequestException)
    }

    def "delete a non-existent server with a valid id unsuccessfully"() {
        given: "a non-existent valid id"
        UUID id = UUID.randomUUID()

        when: "the service is called to delete the server"
        codeUnderTest.deleteServer(id.toString())

        then: "it fails"
        thrown(ResourceNotFoundException)
        1 * serverDao.getServerById(id)
    }
}
