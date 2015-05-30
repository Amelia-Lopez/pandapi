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
### Running
* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html) (or [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html))

### Building
* [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Gradle 2.3+](https://gradle.org/downloads/)

## API
### Servers
#### List servers
Url: /servers
  
Example Request  
```
GET /servers
```
Example Response  
```
[]
```
##### Returns
Returns the list of server resources in the system.

## License
See the [LICENSE](LICENSE.txt) file for license rights and limitations.
