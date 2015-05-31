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

import spock.lang.Shared
import spock.lang.Specification

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
}
