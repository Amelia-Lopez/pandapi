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

import org.eclipse.jetty.http.HttpStatus
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Timeout
import spock.lang.Unroll

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

    // base path for all of the endpoints in this test
    static private final String BASE_PATH = '/v1/servers'

    // servers take 35 seconds to be built
    static private final int SERVER_BUILD_TIME = 35_000

    // REST client to use when calling the Panda API server
    @Shared
    def RESTClient client

    /**
     * This method is called once before running any tests.
     */
    def setupSpec() {
        client = new RESTClient(SERVER_BASE_URL)
    }

    /**
     * This test was useful during initial implementation, but if we ever decided to run these tests in parallel,
     * we would need to remove this test.
     */
    def "retrieve empty list of servers"() {
        given: "default path and headers"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS

        when: "we retrieve the list of servers"
        def response = client.get(path: path, headers: headers)

        then: "we get an empty server list"
        notThrown(HttpResponseException)
        response.status == HttpStatus.OK_200
        response.contentType == JSON.toString()
        response.data == [servers: []]
    }

    // creating a server can take a long time, but we should get a response immediately (less than 1 second)
    @Timeout(1)
    def "create a new server"() {
        given: "a valid request"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest()

        when: "we POST to the endpoint"
        def response = client.post(path: path, requestContentType: JSON, headers: headers, body: request)

        then: "the response contains the same values along with a valid ID and a state of 'Building'"
        notThrown(HttpResponseException)

        // servers take some time to build, so we should get an initial response code of 202
        response.status == HttpStatus.ACCEPTED_202
        response.contentType == JSON.toString()

        // server should give us the URL for the newly created resource
        response.headers.Location == "$BASE_PATH/${response.data.server.id}"

        // values in the response
        isValidUUID(response.data.server.id as String)
        response.data.server.name == request.server.name
        response.data.server.cpus == request.server.cpus
        response.data.server.ram == request.server.ram
        response.data.server.diskSpace == request.server.diskSpace
        response.data.server.status == 'Building'
    }

    @Timeout(1)
    def "retrieve non-empty list of servers"() {
        given: "a server exists"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest(name: 'List Server Test')
        client.post(path: path, requestContentType: JSON, headers: headers, body: request)

        when: "we retrieve the list of servers"
        def response = client.get(path: path, headers: headers)

        then: "we received a list of at least one server"
        notThrown(HttpResponseException)
        response.status == HttpStatus.OK_200
        response.contentType == JSON.toString()
        response.data.servers
    }

    @Timeout(1)
    def "retrieve single server"() {
        given: "a server exists"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest(name: 'Get Individual Server Test')
        def id = client.post(path: path, requestContentType: JSON, headers: headers, body: request).data.server.id

        when: "we retrieve the server"
        def response = client.get(path: "$path/$id", headers: headers)

        then: "we received the server resource"
        notThrown(HttpResponseException)
        response.status == HttpStatus.OK_200
        response.contentType == JSON.toString()

        // make sure we got the "server" field in the response
        response.data.server

        // make sure the IDs match
        response.data.server.id == id
    }

    @Timeout(45)
    def "new server goes into Running state after about 35 seconds"() {
        given: "a valid request"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest()
        def id = client.post(path: path, requestContentType: JSON, headers: headers, body: request).data.server.id

        and: "the server resource has had time to be built"
        sleep(SERVER_BUILD_TIME + 5_000)

        when: "we retrieve the server"
        def response = client.get(path: "$path/$id", headers: headers)

        then: "it's in the Running state"
        notThrown(HttpResponseException)
        response.status == HttpStatus.OK_200
        response.contentType == JSON.toString()
        response.data.server.state == 'Running'
    }

    @Ignore // ain't nobody got time for that
    @Timeout(1)
    def "delete a server"() {
        given: "a server exists"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest(name: 'Get Individual Server Test')
        def id = client.post(path: path, requestContentType: JSON, headers: headers, body: request).data.server.id

        and: "the server resource has had time to be built"
        sleep(SERVER_BUILD_TIME + 5_000)

        when: "we delete the server"
        def response = client.delete(path: "$path/$id", headers: headers)

        then: "the server response is successful"
        notThrown(HttpResponseException)
        response.status == HttpStatus.NO_CONTENT_204
    }

    @Ignore // ain't nobody got time for that
    @Timeout(1)
    def "deleted server is marked destroyed"() {
        given: "a server exists"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest(name: 'Get Individual Server Test')
        def id = client.post(path: path, requestContentType: JSON, headers: headers, body: request).data.server.id

        and: "the server resource has had time to be built"
        sleep(SERVER_BUILD_TIME + 5_000)

        and: "the server was deleted"
        client.delete(path: "$path/$id", headers: headers)

        when: "we retrieve the server"
        def response = client.get(path: "$path/$id", headers: headers)

        then: "the server has the status 'Destroyed'"
        notThrown(HttpResponseException)
        response.status == HttpStatus.OK_200
        response.data.server.status == 'Destroyed'
    }

    @Ignore // ain't nobody got time for that
    @Timeout(1)
    def "deleted server is purged after one minute"() {
        given: "a server exists"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest(name: 'Get Individual Server Test')
        def id = client.post(path: path, requestContentType: JSON, headers: headers, body: request).data.server.id

        and: "the server resource has had time to be built"
        sleep(SERVER_BUILD_TIME + 5_000)

        and: "the server was deleted"
        client.delete(path: "$path/$id", headers: headers)

        and: "the destroyed server resource has had time to be purged"
        sleep(65_000)

        when: "we retrieve the server"
        client.get(path: "$path/$id", headers: headers)

        then: "we get a 404 indicating the server has been purged"
        def e = thrown(HttpResponseException)
        e.response.statusLine.statusCode == HttpStatus.NOT_FOUND_404
    }

    @Unroll
    @Timeout(1)
    def "create server request with invalid values(#name, #cpus, #ram, #diskSpace) fails with message containing #message"() {
        given: "an invalid request"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest(name: name, cpus: cpus, ram: ram, diskSpace: diskSpace)

        when: "we POST to the endpoint"
        client.post(path: path, requestContentType: JSON, headers: headers, body: request)

        then: "the request is unsuccessful"
        def e = thrown(HttpResponseException)
        e.response.statusLine.statusCode == HttpStatus.BAD_REQUEST_400
        e.response.contentType == JSON.toString()
        e.response.data.error.toLowerCase().contains(message)

        where:
        name  | cpus | ram  | diskSpace || message
        null  | 1    | 1    | 1         || 'name'
        ''    | 1    | 1    | 1         || 'name'
        'abc' | 0    | 1    | 1         || 'cpus'
        'abc' | null | 1    | 1         || 'cpus'
        'abc' | 1    | 0    | 1         || 'ram'
        'abc' | 1    | null | 1         || 'ram'
        'abc' | 1    | 1    | 0         || 'diskSpace'
        'abc' | 1    | 1    | null      || 'diskSpace'
    }

    @Unroll
    @Timeout(1)
    def "create server request with invalid field #field with value #value fails"() {
        given: "an invalid request"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest()
        request[field] = 'a'

        when: "we POST to the endpoint"
        client.post(path: path, requestContentType: JSON, headers: headers, body: request)

        then: "the request is unsuccessful"
        def e = thrown(HttpResponseException)
        e.response.statusLine.statusCode == HttpStatus.BAD_REQUEST_400
        e.response.contentType == JSON.toString()
        e.response.data.error.toLowerCase().contains(field)

        where:
        field   | value
        'id'    | 'abc'
        'id'    | null
        'state' | 'Running'
        'state' | null
        'pie'   | 'pecan'
    }

    @Ignore // ain't nobody got time for that
    @Timeout(1)
    def "unsuccessfully try to delete an already destroyed server"() {
        given: "a server exists"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest(name: 'Get Individual Server Test')
        def id = client.post(path: path, requestContentType: JSON, headers: headers, body: request).data.server.id

        and: "the server resource has had time to be built"
        sleep(SERVER_BUILD_TIME + 5_000)

        and: "the server was deleted"
        client.delete(path: "$path/$id", headers: headers)

        when: "we try to delete the server again"
        client.delete(path: "$path/$id", headers: headers)

        then: "the request is unsuccessful"
        def e = thrown(HttpResponseException)
        e.response.statusLine.statusCode == HttpStatus.BAD_REQUEST_400
        e.response.contentType == JSON.toString()
        e.response.data.error.toLowerCase().contains('state')
    }

    def "unsuccessfully try to delete a non-existent server"() {
        given: "an ID for a server that does not exist"
        def path = "$BASE_PATH/fake_id_bro"
        def headers = DEFAULT_HEADERS

        when: "we try to delete the non-existent server"
        client.delete(path: path, headers: headers)

        then: "the request is unsuccessful"
        def e = thrown(HttpResponseException)
        e.response.statusLine.statusCode == HttpStatus.NOT_FOUND_404
        e.response.contentType == JSON.toString()
    }

    def "unsuccessfully try to retrieve a non-existent server"() {
        given: "an ID for a server that does not exist"
        def path = "$BASE_PATH/fake_id_bro"
        def headers = DEFAULT_HEADERS

        when: "we try to retrieve the non-existent server"
        client.get(path: path, headers: headers)

        then: "the request is unsuccessful"
        def e = thrown(HttpResponseException)
        e.response.statusLine.statusCode == HttpStatus.NOT_FOUND_404
        e.response.contentType == JSON.toString()
    }

    @Timeout(1)
    def "unsuccessfully delete a server that is still building"() {
        given: "a server is still building"
        def path = BASE_PATH
        def headers = DEFAULT_HEADERS
        def request = createNewServerRequest(name: 'Get Individual Server Test')
        def id = client.post(path: path, requestContentType: JSON, headers: headers, body: request).data.server.id

        // don't sleep to give the server time to build

        when: "we try to delete the server"
        client.delete(path: "$path/$id", headers: headers)

        then: "the request is unsuccessful"
        def e = thrown(HttpResponseException)
        e.response.statusLine.statusCode == HttpStatus.BAD_REQUEST_400
        e.response.contentType == JSON.toString()
        e.response.data.error.toLowerCase().contains('state')
    }


    /**
     * Create a new server request for the POST method.  All of the values have valid defaults, so nothing is required
     * to be passed in.
     * @param options Map valid values are: name, cpus, ram, diskSpace
     * @return Object usable by the REST client as a request body.
     */
    def createNewServerRequest(Map options = [:]) {
        [server: [
                name: options['name'] as String ?: 'Default Name',
                cpus: options['cpus'] as Integer ?: 4,
                ram: options['ram'] as Integer ?: 8,
                diskSpace: options['diskSpace'] as Integer ?: 40]]
    }

    /**
     * Determines if a UUID is valid by trying to create a UUID instance with the given String.  If any exception
     * is thrown by the UUID class, this method will return false.
     * @param uuid String
     * @return boolean
     */
    def isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid)
            true
        } catch(ignored) {
            false
        }
    }
}
