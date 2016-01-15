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
  * {"favourites":true,"levels":["Fortgeschritten"],"languages":["Englisch"],"tracks":["IDEs & Tools"],"locations":["Wintergarten", "Schauspielhaus"]}
* Lesen:
  * GET
  * http://localhost:8080/rest/filters

## Health Check

Für den Health Check bitte `/health` als URL aufrufen.
Ein Status-Code 200 zeigt an, dass alles in Ordnung ist.

## DB
* H2 im Development-Modus
  * In-Memory (jdbc:h2:mem:testdb)
  * DB-Konsole verfügbar: http://localhost:8080/develop/h2-console/
* PostgreSQL im Profil "postgresql"
  * Aktivieren mit -Dspring.profiles.active=postgresql
  * zum Starten der Tests gegen die PostgreSQL-DB wird das Profil "postgresql-test" benötigt, damit die DB gelöscht wird (-Dspring.profiles.active=postgresql-test)

