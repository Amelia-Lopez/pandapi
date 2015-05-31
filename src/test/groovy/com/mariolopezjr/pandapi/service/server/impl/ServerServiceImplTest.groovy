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
import com.mariolopezjr.pandapi.data.server.ServerUtility
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
}
