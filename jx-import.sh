#!/bin/bash

set -eu

exec jx import --jenkinsfile=Jenkinsxfile --branches='^(master|develop|feature/.*|bugfix/.*)$' --disable-updatebot=true
