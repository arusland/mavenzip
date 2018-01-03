#!/bin/bash

sdir="$(dirname $0)"

echo "Script dir=$sdir"

echo "Killing all started mavenzip instances..."
pgrep -a -f mavenzip.war | awk '{print $1;}' | while read -r a; do kill -9 $a; done

cd $sdir/dist

# jetty-runner
jrunner --port 8080 --path /mavenzip mavenzip.war &
