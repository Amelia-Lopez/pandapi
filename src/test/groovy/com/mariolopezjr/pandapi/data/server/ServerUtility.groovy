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

/**
 * Common utility methods for tests needing a {@link Server} instance.
 * <br/><br/>
 *
 * "There are only two hard things in Computer Science: cache invalidation and naming things."<br/>
 * -- Phil Karlton
 *
 * @author Mario Lopez Jr
 * @since 0.0.6
 */
class ServerUtility {

    /**
     * Generates the specified number of instances of {@link Server}s with junk data.
     * @param numOfInstances int the number of desired instances, 0 or more
     * @return {@link List}<{@link Server}>
     */
    static List<Server> generateServerInstances(final int numOfInstances) {
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

    /**
     * Generates the specified number of instances of {@link Server}s with junk data.  The list is guaranteed to
     * be unsorted according to {@link Server#compareTo}.
     * @param numOfInstances int Value must be =>3.  The list can't be considered unsorted with less than 3 instances.
     * @return {@link List}<{@link Server}>
     */
    static List<Server> generateUnsortedServerInstances(final int numOfInstances) {
        if (numOfInstances < 3) {
            throw new IllegalArgumentException("Less than three instances were requested.")
        }

        List<Server> servers = generateServerInstances(numOfInstances)

        // manually set the first 3 instances' UUIDs to ensure they are not sorted
        servers[0].id = UUID.fromString("0becd947-1b35-4d1b-be28-c3b9576db9ce") // last: e
        servers[1].id = UUID.fromString("0becd947-1b35-4d1b-be28-c3b9576db9cf") // last: f
        servers[3].id = UUID.fromString("0becd947-1b35-4d1b-be28-c3b9576db9cd") // last: d

        servers
    }
}
