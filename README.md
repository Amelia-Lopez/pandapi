Panda API
=========

Panda API is a self-contained RESTful API server built on Jetty, Jersey, and other third party libraries.
For documentation on the available endpoints, see the [API](#API) section below.

## Quick Start
This will start the server and allow you to access the [documented endpoints](#API).  

```
git clone https://github.com/Mario-Lopez/Pandapi.git  
cd pandapi  
gradle clean build  
java -jar build/libs/pandapi.jar  
```

## API
### Servers
#### List servers
Url: /servers
  
Example Response  
```
[]
```
##### Returns
Returns the list of server resources in the system.

## License
See the [LICENSE](LICENSE.txt) file for license rights and limitations.
