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

package com.mariolopezjr.pandapi.data.server

import com.mariolopezjr.pandapi.exception.BadRequestException
import spock.lang.Specification
import spock.lang.Unroll

import static com.mariolopezjr.pandapi.data.server.ServerState.BUILDING
import static com.mariolopezjr.pandapi.data.server.ServerState.DESTROYED
import static com.mariolopezjr.pandapi.data.server.ServerState.RUNNING
import static com.mariolopezjr.pandapi.data.server.ServerState.TERMINATING

/**
 * Unit tests for the {@link Server} class.
 * @author Mario Lopez Jr
 * @since 0.0.6
 */
class ServerTest extends Specification {

    def "clone an instance"() {
        given: "a server instance"
        def server = ServerUtility.generateServerInstances(1).first()

        when: "the server is cloned"
        def clone = server.clone()

        then: "the clone and the server have the same values but are different instances"
        clone == server
        !clone.is(server)
    }

    def "validate a valid create request"() {
        given: "a valid request"
        Server server = new Server(name: 'valid name', cpus: 2, ram: 4, diskSpace: 20)

        when: "the server is validated as a create request"
        server.validateAsCreateRequest()

        then: "it succeeds validation"
        notThrown(Exception)
    }

    @Unroll
    def "validate an invalid create request(#uuid, #name, #cpus, #ram, #diskSpace, #state)"() {
        given: "an invalid request"
        Server server = new Server(id: uuid, name: name, cpus: cpus, ram: ram, diskSpace: diskSpace, state: state)

        when: "the server is validated as a create request"
        server.validateAsCreateRequest()

        then: "it fails validation"
        thrown(BadRequestException)

        where:
        name  | cpus | ram | diskSpace | state       | uuid
        ''    | 1    | 2   | 6         | null        | null
        null  | 2    | 4   | 8         | null        | null
        'db'  | 0    | 8   | 10        | null        | null
        'dat' | -1   | 16  | 20        | null        | null
        'web' | 1    | 0   | 6         | null        | null
        'mid' | 2    | -1  | 8         | null        | null
        'db'  | 4    | 8   | 0         | null        | null
        'dat' | 8    | 16  | -1        | null        | null
        'web' | 1    | 2   | 6         | BUILDING    | null
        'mid' | 2    | 4   | 8         | RUNNING     | null
        'db'  | 4    | 8   | 10        | TERMINATING | null
        'dat' | 8    | 16  | 20        | DESTROYED   | null
        'dat' | 8    | 16  | 40        | null        | UUID.randomUUID()
    }

    def "test clone and equals"() {
        given: "a server instance"
        Server server = new Server(
                id: UUID.randomUUID(),
                name: 'elephant',
                cpus: 2,
                ram: 4,
                diskSpace: 100,
                state: RUNNING)

        when: "it is cloned"
        Server clone = server.clone()

        then: "the two instances have identical field values but are different instances"
        server == clone
        !server.is(clone)
    }
}
