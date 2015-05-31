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

package com.mariolopezjr.pandapi.service.server.impl;

import com.mariolopezjr.pandapi.data.server.Server;

import java.util.Comparator;

/**
 * Comparator for {@link Server} objects that compares them by their {@link Server#id} only.
 * @author Mario Lopez Jr
 * @since 0.0.6
 */
public class ServerUUIDComparator implements Comparator<Server> {

    /**
     * Compares the two {@link Server}s by their {@link java.util.UUID}.  Null instances are not permitted.
     * @param o1 {@link Server}
     * @param o2 {@link Server}
     * @return int
     * @throws NullPointerException if either of the {@link Server} instances are null.
     */
    @Override
    public int compare(Server o1, Server o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
