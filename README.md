# DukeCon Server

## Build & Dependency Status

* [![Build Status](https://travis-ci.org/dukecon/dukecon_server.svg?branch=master)](https://travis-ci.org/dukecon/dukecon_server)

* API: [![Dependency Status API](https://www.versioneye.com/user/projects/5552099d06c318a32a0000c5/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5552099d06c318a32a0000c5)
* Impl: [![Dependency Status Impl](https://www.versioneye.com/user/projects/555209a206c3183055000123/badge.svg?style=flat)](https://www.versioneye.com/user/projects/555209a206c3183055000123)

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
