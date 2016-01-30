#!/usr/bin/env bash
# this is a wrapper for Xvfb to be able to run tests in headless CI environments
# Use 
#   mvn -Djvm="./xvfb.sh"
# so that Surefire runs your tests in a Xvfb wrapped Java process
/usr/bin/xvfb-run $JAVA_HOME/bin/java $@
