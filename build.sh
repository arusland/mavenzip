#!/bin/bash

rm ./dist/*.war

mvn clean package -DskipTests
cp ./target/allinone-*.war ./dist/mavenzip.war
