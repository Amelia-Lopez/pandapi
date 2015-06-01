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

import com.mariolopezjr.pandapi.exception.BadRequestException;
import com.mariolopezjr.pandapi.web.document.ErrorMessageDoc;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey mapper to map a thrown {@link BadRequestException} to a 400 status.
 * @author Mario Lopez Jr
 * @since 0.0.7
 */
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(BadRequestException exception) {
        // no need to log this exception, the client will know what they did

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorMessageDoc.message(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
