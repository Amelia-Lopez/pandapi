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
import com.mariolopezjr.pandapi.service.server.ServerService
import com.mariolopezjr.pandapi.web.document.server.ServerGetListResponse
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

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

    def setup() {
        // mocks
        serverService = Mock(ServerService)

        codeUnderTest = new ServerApi(serverService)
    }

    @Unroll
    def "retrieve list of servers when there are #instances instance(s) in the data store"() {
        when:
        ServerGetListResponse response = codeUnderTest.servers

        then:
        1 * serverService.allServers >> generateServerInstances(instances)
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

    /**
     * Generates the specified number of instances of {@link Server}s with junk data.
     * @param numOfInstances int the number of desired instances, 0 or more
     * @return {@link List}<{@link Server}>
     */
    private static List<Server> generateServerInstances(final int numOfInstances) {
        if (numOfInstances == 0) {
            return []
        }

        // create a list of server instances; use the current number to populate most of the fields
        (1..numOfInstances).collect { int num ->
            Server server = new Server()

            server.with {
                id = UUID.randomUUID()
                name = "$num"
                cpus = num
                ram = num
                diskSpace = num
                state = ServerState.RUNNING
            }

            // instance will be added to the list being returned
            server
        }
    }
}
