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

    public void setCpus(int cpus) {
        this.cpus = cpus;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(int diskSpace) {
        this.diskSpace = diskSpace;
    }

    public ServerState getState() {
        return state;
    }

    public void setState(ServerState state) {
        this.state = state;
    }

    @Override
    public Server clone() throws CloneNotSupportedException {
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
