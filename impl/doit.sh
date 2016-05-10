#!/bin/bash

set -eu

INSPECTIT="-javaagent:$HOME/wrk/dukecon/inspectit/1.6.6/agent/inspectit-agent.jar -Dinspectit.config=$HOME/wrk/dukecon/dukecon_server/impl/config" # -Dinspectit.repository=10.211.55.45:9070;dukecon"

#export SPRING_PROFILES_ACTIVE=noauth

java ${INSPECTIT} -Dspring.profiles.active=noauth -jar target/dukecon-server-springboot-1.2.0-SNAPSHOT.war
