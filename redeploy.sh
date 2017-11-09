#!/bin/bash

sdir="$(dirname $0)"

echo "Script dir=$sdir"

cd $sdir

git reset --hard HEAD
git pull

mvn clean package -DskipTests
OUT=$?

if [ $OUT != 0 ]; then
   echo "Project rebuild failed: $OUT!"
   exit $OUT
fi

echo "Killing all started matebot instances..."
pgrep -a -f mavenzip.war | awk '{print $1;}' | while read -r a; do kill -9 $a; done

rm ./dist/*.war
cp ./target/allinone-1.0.war ./dist/mavenzip.war

cd $sdir/dist

# jetty-runner
jrunner --port 80 --path /mavenzip mavenzip.war &
