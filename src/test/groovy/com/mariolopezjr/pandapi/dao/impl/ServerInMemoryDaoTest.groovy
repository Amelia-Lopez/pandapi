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

package com.mariolopezjr.pandapi.dao.impl

import com.mariolopezjr.pandapi.data.server.Server
import com.mariolopezjr.pandapi.data.server.ServerUtility
import com.mariolopezjr.pandapi.exception.InternalException
import org.apache.commons.configuration.Configuration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Unroll

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Unit test(s) for {@link ServerInMemoryDao} class.
 * @author Mario Lopez Jr
 * @since 0.0.6
 */
class ServerInMemoryDaoTest extends Specification {

    @Shared
    private ServerInMemoryDao codeUnderTest

    @Shared
    private ConcurrentMap<UUID, Server> dataStore

    @Shared
    private Configuration config

    def setup() {
        // set initial size of map to 200 to avoid resizing during these tests
        dataStore = new ConcurrentHashMap<>(200)

        config = Mock(Configuration) {
            getInt(_ as String, _ as Integer) >>> [1024, 100]
            getFloat(_ as String, _ as Float) >> 0.75f
        }

        codeUnderTest = new ServerInMemoryDao(dataStore, config)
    }

    @Unroll
    def "retrieve list of servers when we get back #instances instance(s) from the data store map"() {
        given: "the map has #instance entries in it"
        dataStore << ServerUtility.generateServerInstances(instances).collectEntries {[(it.id): it]}

        when: "the DAO is called to ge the list of all of the servers"
        List<Server> response = codeUnderTest.allServers

        then: "we get the correct number of instances back"
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

    def "client code is not allowed to add instances to the data store directly"() {
        given: "the list of servers returned from the DAO"
        dataStore << ServerUtility.generateServerInstances(5).collectEntries {[(it.id): it]}
        List<Server> servers = codeUnderTest.allServers

        when: "we try to add an item to the returned list"
        servers.add(ServerUtility.generateServerInstances(1).first())

        then: "we get an exception"
        thrown(UnsupportedOperationException)
    }

    def "client code is not allowed to delete instances from the data store directly"() {
        given: "the list of servers returned from the DAO"
        dataStore << ServerUtility.generateServerInstances(5).collectEntries {[(it.id): it]}
        List<Server> servers = codeUnderTest.allServers

        when: "we try to remove an item from the returned list"
        servers.remove(0)

        then: "we get an exception"
        thrown(UnsupportedOperationException)
    }

    def "client code is not allowed to modify instances in the data store directly"() {
        given: "the list of servers returned from the DAO"
        dataStore << ServerUtility.generateServerInstances(1).collectEntries {[(it.id): it]}
        List<Server> servers = codeUnderTest.allServers

        when: "we try to modify the name of one of the servers"
        servers[0].name = "Some New Name"

        then: "the update does not get persisted to the data store"
        servers[0].name != codeUnderTest.allServers[0].name
    }

    def "retrieve a server"() {
        given: "an existing server with a valid id"
        Server existingServer = ServerUtility.generateServerInstances(1).first()
        dataStore.put(existingServer.id, existingServer)

        when: "the DAO is called to retrieve the server"
        Server response = codeUnderTest.getServerById(existingServer.id)

        then: "we got our server back"
        response == existingServer
    }

    def "updates to a retrieved server does not affect the instance in the data store"() {
        given: "an existing server with a valid id"
        Server existingServer = ServerUtility.generateServerInstances(1).first()
        dataStore.put(existingServer.id, existingServer)

        and: "the DAO was called to retrieve the server"
        Server response = codeUnderTest.getServerById(existingServer.id)

        when: "the retrieved server name is updated"
        response.name = 'Look at me updating this name'

        then: "the server instance in the data store is not affected"
        dataStore.get(existingServer.id).name != response.name
    }

    @Timeout(1)
    def "create a server"() {
        given: "a valid server instance"
        Server newServer = ServerUtility.generateServerInstances(1).first()
        newServer.id = null   // DAO sets the ID

        when: "the DAO is called to create the server"
        Server response = codeUnderTest.createServer(newServer)

        then: "the server is added to the data store"
        notThrown(InternalException)
        response.id
        response.name == newServer.name
        response.cpus == newServer.cpus
        response.ram == newServer.ram
        response.diskSpace == newServer.diskSpace
        response.state == newServer.state
        !response.is(newServer)
    }

    def "create a server that already has an ID unsuccessfully"() {
        given: "an otherwise valid server instance that incorrectly has an ID already"
        Server newServer = ServerUtility.generateServerInstances(1).first()

        when: "the DAO is called to create the server"
        codeUnderTest.createServer(newServer)

        then: "an exception is thrown"
        thrown(InternalException)
    }

    def "create a server and try to update the data store entity with the old object unsuccessfully"() {
        given: "a valid server instance"
        Server serverRequest = ServerUtility.generateServerInstances(1).first()
        serverRequest.id = null   // DAO sets the ID

        and: "a new name to set later"
        String newName = 'Let us see if this works!'

        and: "the DAO was called to create the server"
        Server response = codeUnderTest.createServer(serverRequest)

        when: "the name of the original server is updated"
        serverRequest.name = newName

        then: "the instance in the data store is not affected"
        dataStore.get(response.id).name != newName
    }

    def "create a server and try to update the data store entity with the new object unsuccessfully"() {
        given: "a valid server instance"
        Server serverRequest = ServerUtility.generateServerInstances(1).first()
        serverRequest.id = null   // DAO sets the ID

        and: "a new name to set later"
        String newName = 'Let us see if this works!'

        and: "the DAO was called to create the server"
        Server response = codeUnderTest.createServer(serverRequest)

        when: "the name of the response server is updated"
        response.name = newName

        then: "the instance in the data store is not affected"
        dataStore.get(response.id).name != newName
    }

    def "update a server"() {
        given: "an existing server in the data store"
        Server existingServer = ServerUtility.generateServerInstances(1).first()
        dataStore.put(existingServer.id, existingServer)

        and: "an update for that server"
        Server updatedServer = existingServer.clone()
        updatedServer.name = 'Better name than before'

        when: "the DAO is called to update the server"
        boolean wasSuccessful = codeUnderTest.updateServer(updatedServer)

        then: "the server was updated in the data store"
        wasSuccessful
        dataStore.get(existingServer.id) == updatedServer
    }

    def "update a non-existing server unsuccessfully"() {
        given: "an ID for a non-existing server"
        UUID id = UUID.randomUUID()

        and: "an update for that non-existent server"
        Server updatedServer = ServerUtility.generateServerInstances(1).first()
        updatedServer.id = id

        when: "the DAO is called to update the server"
        boolean wasSuccessful = codeUnderTest.updateServer(updatedServer)

        then: "the server was not added to the data store"
        !wasSuccessful
        !dataStore.containsKey(id)
    }

    def "updates to a server that was just updated does not affect the instance in the data store"() {
        given: "an existing server in the data store"
        Server existingServer = ServerUtility.generateServerInstances(1).first()
        dataStore.put(existingServer.id, existingServer)

        and: "an update for that server"
        Server updatedServer = existingServer.clone()
        updatedServer.name = 'Better name than before'

        and: "the DAO was called to update the server"
        codeUnderTest.updateServer(updatedServer)

        when: "the original server instance (given to the update method) is updated"
        updatedServer.name = 'The best name ever'

        then: "the server instance in the data store was not affected"
        dataStore.get(updatedServer.id).name != updatedServer.name
    }

    def "delete an existing server"() {
        given: "an existing server"
        Server existingServer = ServerUtility.generateServerInstances(1).first()
        dataStore.put(existingServer.id, existingServer)

        when: "the DAO is called to delete the server"
        boolean wasSuccessful = codeUnderTest.deleteServer(existingServer.id)

        then: "it was deleted"
        wasSuccessful
        !dataStore.containsKey(existingServer.id)
    }

    def "delete a non-existent server unsuccessfully"() {
        given: "an ID for a non-existent server"
        UUID id = UUID.randomUUID()

        when: "the DAO is called to delete the server"
        boolean wasSuccessful = codeUnderTest.deleteServer(id)

        then: "nothing was deleted"
        !wasSuccessful
    }
}
