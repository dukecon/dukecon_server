# dukecon_server

## Build & Dependency Status

* [![Build Status](https://travis-ci.org/dukecon/dukecon_server.svg?branch=master)](https://travis-ci.org/dukecon/dukecon_server)

* API: [![Dependency Status API](https://www.versioneye.com/user/projects/5552099d06c318a32a0000c5/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5552099d06c318a32a0000c5)
* Impl: [![Dependency Status Impl](https://www.versioneye.com/user/projects/555209a206c3183055000123/badge.svg?style=flat)](https://www.versioneye.com/user/projects/555209a206c3183055000123)

## REST Services

### Talks

### Meta-Informationen

### User-Preferences (Vortragsfavoriten)

### User-Filter
* Login über Keycloak
* Datenablage je Principal in der Datenbank
* Speichern:
  * PUT
  * http://localhost:8080/rest/filters
  * Content-Type: application/json
  * {"favourites":true,"levels":["Fortgeschritten"],"languages":["Englisch"],"tracks":["IDEs & Tools"],"rooms":["Wintergarten", "Schauspielhaus"]}
* Lesen:
  * GET
  * http://localhost:8080/rest/filters

## DB

* im Moment H2, später wahrscheinlich PostgreSQL
* DB-Konsole für die H2: http://localhost:8080/console