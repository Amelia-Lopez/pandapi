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

import com.mariolopezjr.pandapi.exception.InternalException;
import com.mariolopezjr.pandapi.web.document.ErrorMessageDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Jersey mapper to map a thrown {@link InternalException} to a 400 status.
 * @author Mario Lopez Jr
 * @since 0.0.7
 */
@Provider
public class InternalExceptionMapper implements ExceptionMapper<InternalException> {

    // slf4j logger
    private static final Logger LOG = LoggerFactory.getLogger(InternalExceptionMapper.class);

    // so we can see what the client was asking for
    @Context
    private UriInfo uriInfo;

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(InternalException exception) {
        LOG.error("Internal error during client request for URI: {}", uriInfo.getAbsolutePath(), exception);

        // no need to tell the client about our problems
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorMessageDoc.message("Internal Server Error"))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
