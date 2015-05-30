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

package com.mariolopezjr.pandapi.integrationtests.functional

import spock.lang.Specification

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient

import static groovyx.net.http.ContentType.JSON

/**
 * Tests the functional requirements for the Server API.
 *
 * These include:
 * - Verifying a server resource can be created
 * - Verifying a server resource can be retrieved
 * - Verifying a server resource can be destroyed
 *
 * @author Mario Lopez Jr
 * @since 0.0.3
 */
class ServerSpec extends Specification {

    // REST server details
    static private final String PORT = '8080'   // todo: need to make this configurable
    static private final String SERVER_BASE_URL = "http://localhost:${PORT}"

    // default REST headers that all calls should use
    static private final Map<String, String> DEFAULT_HEADERS = [Connection: 'close']

    // REST client to use when calling the Panda API server
    private RESTClient client = new RESTClient(SERVER_BASE_URL)


    def "retrieve list of servers"() {
        given:
        def path = '/servers'
        def headers = DEFAULT_HEADERS

        when:
        def response = client.get(path: path, headers: headers)

        then:
        notThrown(HttpResponseException)
        response.status == 200
        response.contentType == JSON.toString()
        response.data.text.toString() == '[]'
    }
}
