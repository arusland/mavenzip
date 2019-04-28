#!/bin/bash

docker stop mavenzip

docker rm mavenzip

docker run -d --restart=always -p 127.0.0.1:8080:8080 --name mavenzip arusland/mavenzip
