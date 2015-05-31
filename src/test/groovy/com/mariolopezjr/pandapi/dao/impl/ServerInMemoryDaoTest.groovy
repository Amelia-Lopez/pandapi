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
import spock.lang.Shared
import spock.lang.Specification
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

    def setup() {
        // set initial size of map to 200 to avoid resizing during these tests
        dataStore = new ConcurrentHashMap<>(200)

        codeUnderTest = new ServerInMemoryDao(dataStore)
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
}
