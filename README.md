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
```
{
  "servers": []
}
```
##### Returns
Returns the list of server resources in the system.

## License
See the [LICENSE](LICENSE.txt) file for license rights and limitations.
