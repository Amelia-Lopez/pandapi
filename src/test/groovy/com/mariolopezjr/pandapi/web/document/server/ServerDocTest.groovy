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

package com.mariolopezjr.pandapi.web.document.server

import com.mariolopezjr.pandapi.data.server.Server
import com.mariolopezjr.pandapi.exception.NonCorrespondingValueException
import spock.lang.Specification
import spock.lang.Unroll

import static com.mariolopezjr.pandapi.web.document.server.ServerStateDoc.*
import static com.mariolopezjr.pandapi.data.server.ServerState.*

/**
 * Unit tests for the {@link ServerDoc} class.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
class ServerDocTest extends Specification {

    @Unroll
    def "translate Server(#uuid, #name, #cpus, #ram, #diskSpace, #state) to ServerDoc"() {
        given: "A Server instance"
        def server = new Server(id: uuid, name: name, cpus: cpus, ram: ram, diskSpace: diskSpace, state: state)

        when:
        ServerDoc serverDoc = ServerDoc.fromDomainObject(server)

        then:
        serverDoc
        serverDoc.id == server.id
        serverDoc.name == server.name
        serverDoc.cpus == server.cpus
        serverDoc.ram == server.ram
        serverDoc.diskSpace == server.diskSpace
        serverDoc.state == stateDoc

        where:
        name  | cpus | ram | diskSpace | state       | stateDoc  | uuid
        'web' | 1    | 2   | 6         | BUILDING    | Building  | UUID.randomUUID()
        'mid' | 2    | 4   | 8         | RUNNING     | Running   | UUID.randomUUID()
        'db'  | 4    | 8   | 10        | TERMINATING | Destroyed | UUID.randomUUID()
        'dat' | 8    | 16  | 20        | DESTROYED   | Destroyed | UUID.randomUUID()
    }

    def "null server state throws correct exception"() {
        given: "A Server instance with a null server state"
        def server = new Server(id: UUID.randomUUID(), name: 'a', cpus: 1, ram: 1, diskSpace: 1, state: null)

        when:
        ServerDoc.fromDomainObject(server)

        then:
        thrown(NonCorrespondingValueException)
    }
}
