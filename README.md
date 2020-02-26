# DukeCon Server

## Modules

### api

Domain model, used by **repositories**, **converters** and **impl**.

### resources

Static resources as images (logo, favicon, track/stream images) and templates needed for different conferences. 
Used by **converters**, was used by **impl**.

### repositories

Contains logic to convert conference input to internal data structure to be served to client applications. 

### impl

Server implementation with dynamic features as favorites, preferences and keycloak configuration.

Formerly it also contained dynamically served conference data which was now moved to **converters** where it will be generated as files which will be served as static content from edge service.
This feature is enabled by default but can be deactivated as Spring Boot configuration value `conferences.read`, e.g., at startup with `--conferences.read=false`. 

### converters

Parameterizable standalone java application for reading conference input and saving json files and images to serve from edge server statically. 
This content was generated dynamically in **impl** lately and was moved to **converters**. 

## Build & Dependency Status

* [![Build Status](https://travis-ci.org/dukecon/dukecon_server.svg?branch=master)](https://travis-ci.org/dukecon/dukecon_server)

* Reactor: [![Dependency Status](https://www.versioneye.com/user/projects/56f80143ed7236000ac3f3f1/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56f80143ed7236000ac3f3f1)
* API: [![Dependency Status](https://www.versioneye.com/user/projects/56f8034335630e0029db09a6/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56f8034335630e0029db09a6)
* Impl: [![Dependency Status](https://www.versioneye.com/user/projects/56f8034735630e003888ac53/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56f8034735630e003888ac53)

## REST Services

### Talks

Accessible on `/rest/conferences/499959`.
Data is being cached.

Data can be updated with URI `/rest/conferences/update/499959`.
Therefor an authenticated request with role `ROLE_ADMIN` is needed.

### Meta-Information

### User-Preferences (Talk favorites)

### User-Filter
* Login with Keycloak
* Filters will be persisted with a record of each principal in the DB
* Write/save:
  * HTTP method `PUT` 
  * URL `http://localhost:8080/rest/filters`
  * Content-Type: `application/json`
  * Payload: `{"favourites":true,"levels":["Fortgeschritten"],"languages":["Englisch"],"tracks":["IDEs & Tools"],"locations":["Wintergarten", "Schauspielhaus"]}`
* Read:
  * HTTP method `GET`
  * URL `http://localhost:8080/rest/filters`

## Health Check

Health check is available at `/health` URI.  
HTTP status code `200` of the response tells you that everything is ok.

## DB
* H2 in development mode
  * In-Memory (`jdbc:h2:mem:testdb`)
  * DB console: `http://localhost:8080/develop/h2-console/`
* PostgreSQL with profile _"postgresql"_
  * activate with `-Dspring.profiles.active=postgresql`
  * to run the tests against the PostgreSQL db (and a previously reset of the db), you need the _"postgresql-test"_ profile (`-Dspring.profiles.active=postgresql-test`)

## Dependency Management
* Maven build fails in verify phase if declared dependencies are unused or used dependencies are undeclared
  * mvn verify
  * CI calls mvn deploy which includes verify
  * the acutal goal is mvn dependency:analyze(-only) which may show warnings
* dependency analyzing may cause problems because of Spring Boot starter dependencies
  * configure <ignoredUnusedDeclaredDependencies> in pom.xml
  
## Development

Start _org.dukecon.DukeConServerApplication_ from your IDE. 
