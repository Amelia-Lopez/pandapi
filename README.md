Panda API
=========

Panda API is a self-contained RESTful API server built on Jetty, Jersey, and a few other third party libraries.
For documentation on the available endpoints, see the [API](#api) section below.

## Quick Start
This will start the server and allow you to access the [available endpoints](#api).  

```console  
git clone https://github.com/Mario-Lopez/pandapi.git  
cd pandapi  
gradle clean build  
java -jar build/libs/pandapi.jar  
```

## Requirements
* [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Gradle 2.4](https://gradle.org/downloads/)

## Building
### Clean Old Build Files
To clean all of the files created during previous builds:
  
```console
gradle clean  
```

### Build an Executable JAR
To build the pandapi.jar (written to $repo/build/libs) with all of the necessary dependencies included:
  
```console
gradle build  
```

### Run Integration Tests
A Gradle task was added to run the integration tests against a locally running server (which
Gradle will also start and stop).
  
```console
gradle integrationTest  
```

### Generate IntelliJ Project Files
Gradle can generate the IntelliJ project files for the source code which will include all of the dependencies.
  
```console  
gradle idea
```

## Running
### Start the Server
Use the Java launcher to run the JAR.  The built JAR is located in $repo/build/libs.
  
```console
java -jar pandapi.jar  
```

### Stop the Server
If you ran the above command, simply press Control-C to gracefully stop the server.

## API
### Servers
#### List servers
Url: /v1/servers
  
Example Request  
```
GET /v1/servers
```
Example Response  
```JSON
{
  "servers": [
    {
      "id": "ada94535-a3a9-4b2c-a383-f36b0bbaab44",
      "name": "testServer",
      "cpus": 4,
      "ram": 8,
      "diskSpace": 40,
      "state": "Running"
    },
    {
      "id": "aea4f0fd-2f70-427d-b026-fec789984fe4",
      "name": "webServer",
      "cpus": 2,
      "ram": 4,
      "diskSpace": 20,
      "state": "Building"
    }
  ]
}
```
##### Returns
* 200 - Returns the list of server resources in the system

#### Create a server
Building a new server instance can take over 30 seconds.  This endpoint will return immediately with an HTTP Accepted
status or a Bad Request status.  If the request is accepted, the response will contain the HTTP "Location" header
containing the URI of the new resource (that can be used to check on the resource's state).  
Url: /v1/servers  
  
Example Request  
```
POST /v1/servers
```
```JSON
{
  "server": {
    "name": "webServer",
    "cpus": 2,
    "ram": 4,
    "diskSpace": 20
  }
}
```
Example Response (202 Accepted)  
```JSON
{
  "server": {
    "id": "aea4f0fd-2f70-427d-b026-fec789984fe4",
    "name": "webServer",
    "cpus": 2,
    "ram": 4,
    "diskSpace": 20,
    "state": "Building"
  }
}
```
Example Response (400 Bad Request)  
```JSON
{
  "error": "Name must be specified, Number of CPUs should be 1 or higher"
}
```
##### Arguments
* name (required)
  : Name of the server, cannot be an empty string
* cpus (required)
  : Number of CPUs the server should be built with, minimum of 1
* ram (required)
  : Amount of RAM the server should be built with in gigabytes, minimum of 1
* diskSpace (required)
  : Amount of disk space on the boot disk for the server in gigabytes, minimum of 1
##### Returns
* 202 - Request was accepted, response will contain the newly created resource with a new unique ID  
* 400 - Request is invalid, response will contain an error message detailing the issue  
#### Retrieve a server
Url: /v1/servers/:id  
  
Example Request
```
GET /v1/servers/4e0b19f0-ef4a-4de6-b3a7-4dd74d6a39bd
```
Example Response
```JSON
{
  "server": {
    "cpus": 4,
    "diskSpace": 40,
    "id": "c331c428-1e31-4f50-a7b6-aef504868007",
    "name": "testServer",
    "ram": 8,
    "state": "Destroyed"
  }
}
```
##### Returns
* 200 - Returns the server resource
* 404 - A server resource with the specified ID could not be found
#### Delete a server
Url: /v1/servers/:id
  
Example Request
```
DELETE /v1/servers/4e0b19f0-ef4a-4de6-b3a7-4dd74d6a39bd
```
Example Response (400 Bad Request)
```JSON
{
  "error": "Only servers in the running state can be destroyed"
}
```
##### Returns
204 - Indicates the resource was destroyed, there will be no body in the response
400 - Indicates the resource was not in a valid state for deleting or the specified ID was malformed, server must be in
the running state to be deleted
404 - Indicates a resource with the specified ID was not found

## License
See the [LICENSE](LICENSE.txt) file for license rights and limitations.
