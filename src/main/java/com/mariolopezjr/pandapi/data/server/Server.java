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

package com.mariolopezjr.pandapi.data.server;

import com.mariolopezjr.pandapi.exception.BadRequestException;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data model for a server resource.
 * @author Mario Lopez Jr
 * @since 0.0.5
 */
public class Server implements Cloneable, Comparable<Server> {

    private UUID id;
    private String name;
    private int cpus;
    private int ram;
    private int diskSpace;
    private ServerState state;


    /**
     * Will verify that the contents of this instance are valid for the purpose of creating a new server resource.
     * Note: This could be way more generic.
     */
    public void validateAsCreateRequest() {
        List<String> errors = new ArrayList<>();

        // shouldn't have an id already specified
        if (id != null) {
            errors.add("IDs are generated by the server and should not be specified by the client");
        }

        if (name == null || name.isEmpty()) {
            errors.add("Name must be specified");
        }

        if (cpus < 1) {
            errors.add("Number of CPUs should be 1 or higher");
        }

        if (ram < 1) {
            errors.add("Amount of RAM should be 1 (gigabyte) or higher");
        }

        if (diskSpace < 1) {
            errors.add("Amount of diskSpace should be 1 (gigabyte) or higher");
        }

        if (state != null) {
            errors.add("Server state should not be specified by the client");
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException(Joiner.on(",").join(errors));
        }
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

    public int getCpus() {
        return cpus;
    }

    public void setCpus(Integer cpus) {
        if (null == cpus) {
            throw new BadRequestException("CPUs is not allowed to be null");
        }

        this.cpus = cpus;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        if (null == ram) {
            throw new BadRequestException("RAM is not allowed to be null");
        }

        this.ram = ram;
    }

    public int getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(Integer diskSpace) {
        if (null == diskSpace) {
            throw new BadRequestException("DiskSpace is not allowed to be null");
        }

        this.diskSpace = diskSpace;
    }

    public ServerState getState() {
        return state;
    }

    public void setState(ServerState state) {
        this.state = state;
    }

    @Override
    public Server clone() {
        Server clone = new Server();

        clone.id = this.id;        // immutable, re-use the object
        clone.name = this.name;    // immutable, re-use the object
        clone.cpus = this.cpus;
        clone.ram = this.ram;
        clone.diskSpace = this.diskSpace;
        clone.state = this.state;

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (cpus != server.cpus) return false;
        if (ram != server.ram) return false;
        if (diskSpace != server.diskSpace) return false;
        if (!id.equals(server.id)) return false;
        if (!name.equals(server.name)) return false;
        return state == server.state;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + cpus;
        result = 31 * result + ram;
        result = 31 * result + diskSpace;
        result = 31 * result + state.hashCode();
        return result;
    }

    @Override
    public int compareTo(Server o) {
        // when comparing instances of this class, you should almost never have two instances with the same UUID
        int uuidComparison = this.id.compareTo(o.id);
        if (uuidComparison != 0) {
            return uuidComparison;
        }

        // something funny is going on...  compare the rest of the fields starting with name
        int nameComparison = this.name.compareTo(o.name);
        if (nameComparison != 0) {
            return nameComparison;
        }

        return (this.cpus < o.cpus ? -1 :
                (this.cpus > o.cpus ? 1 :
                 (this.ram < o.ram ? -1 :
                  (this.ram > o.ram ? 1 :
                   (this.diskSpace < o.diskSpace ? -1 :
                    (this.diskSpace > o.diskSpace ? 1 :
                     (this.state.compareTo(o.state))))))));
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cpus=" + cpus +
                ", ram=" + ram +
                ", diskSpace=" + diskSpace +
                ", state=" + state +
                '}';
    }
}
