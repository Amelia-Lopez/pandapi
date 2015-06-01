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

import com.mariolopezjr.pandapi.data.server.Server;

import java.util.UUID;

/**
 * The data model for the API server resource.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
public class ServerDoc {

    private UUID id;
    private String name;
    private Integer cpus;
    private Integer ram;
    private Integer diskSpace;
    private ServerStateDoc state;

    /**
     * Creates a new instance of this document with the relevant values from the provided domain object.
     * @param domainObject {@link Server}
     * @return {@link ServerDoc}
     */
    public static ServerDoc fromDomainObject(final Server domainObject) {
        ServerDoc doc = new ServerDoc();

        // how I long for Groovy Canonical constructors and Scala case classes
        doc.setId(domainObject.getId());
        doc.setName(domainObject.getName());
        doc.setCpus(domainObject.getCpus());
        doc.setRam(domainObject.getRam());
        doc.setDiskSpace(domainObject.getDiskSpace());
        doc.setState(ServerStateDoc.fromDomainObject(domainObject.getState()));

        return doc;
    }

    /**
     * Creates a new instance of the domain object with the relevant values from this document.
     * @return {@link Server}
     */
    public Server toDomainObject() {
        Server domainObject = new Server();

        domainObject.setId(this.getId());
        domainObject.setName(this.getName());
        domainObject.setCpus(this.getCpus());
        domainObject.setRam(this.getRam());
        domainObject.setDiskSpace(this.getDiskSpace());
        domainObject.setState(this.getState() == null ? null : this.getState().toDomainObject());

        return domainObject;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCpus() {
        return cpus;
    }

    public void setCpus(Integer cpus) {
        this.cpus = cpus;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public Integer getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(Integer diskSpace) {
        this.diskSpace = diskSpace;
    }

    public ServerStateDoc getState() {
        return state;
    }

    public void setState(ServerStateDoc state) {
        this.state = state;
    }
}
