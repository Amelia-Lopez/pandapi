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

package com.mariolopezjr.pandapi.web.document.server;

/**
 * The data model for the POST request for a resource.  Not all of the {@link ServerDoc} fields are
 * necessarily allowed to be set by the client.  This will be validated by the service.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
public class ServerPostRequest {

    private ServerDoc server;


    public ServerDoc getServer() {
        return server;
    }

    public void setServer(ServerDoc server) {
        this.server = server;
    }
}
