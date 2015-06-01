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

package com.mariolopezjr.pandapi.web.document;

/**
 * Simple error message document that can be used as a response in error conditions.
 * @author Mario Lopez Jr
 * @since 0.0.7
 */
public class ErrorMessageDoc {

    private String error;

    /**
     * Convenience method to easily create a new instance of this document.
     * @param message {@link String}
     * @return {@link ErrorMessageDoc}
     */
    public static ErrorMessageDoc message(final String message) {
        ErrorMessageDoc doc = new ErrorMessageDoc();
        doc.setError(message);
        return doc;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
