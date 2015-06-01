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

import com.mariolopezjr.pandapi.service.server.ServerService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Specify our injection bindings for the Server service.  This class will be loaded by the
 * {@link com.mariolopezjr.pandapi.web.application.PandapiRestServer}.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
public class ServerServiceBinder extends AbstractBinder {

    /**
     * Configure injection bindings definitions
     */
    @Override
    protected void configure() {
        bind(ServerServiceImpl.class).to(ServerService.class).in(Singleton.class);
    }
}
