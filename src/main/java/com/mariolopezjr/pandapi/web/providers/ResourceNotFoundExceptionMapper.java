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

package com.mariolopezjr.pandapi.web.providers;

import com.mariolopezjr.pandapi.exception.ResourceNotFoundException;
import com.mariolopezjr.pandapi.web.document.ErrorMessageDoc;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey mapper to map a thrown {@link ResourceNotFoundException} to a 404 status.
 * @author Mario Lopez Jr
 * @since 0.0.7
 */
@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        // no need to log this exception, the access logs should be sufficient

        return Response.status(Response.Status.NOT_FOUND)
                .entity(ErrorMessageDoc.message(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
