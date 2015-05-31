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

import com.mariolopezjr.pandapi.data.server.ServerUtility
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

    /**
     * Set up the shared fields before each test.
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
}
