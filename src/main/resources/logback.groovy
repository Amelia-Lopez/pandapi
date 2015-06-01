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

// use the current directory
def logHome = "."

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %-5level [%-15thread] %+36logger{36} - %msg%n"
    }
}

appender("FILE", RollingFileAppender) {
    file = "${logHome}/pandapi.log"
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "${logHome}/pandapi.%i.log.zip"
        minIndex = 1
        maxIndex = 3
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = "20MB"
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %-5level [%-15thread] %+36logger{36} - %msg%n"
    }
}

/**
 * Set the log levels for individual Java packages and the global log level.
 * Valid log levels are: OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL
 */
logger("com.mariolopezjr.pandapi", TRACE)

root(INFO, ["FILE"])