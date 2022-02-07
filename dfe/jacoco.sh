#!/usr/bin/env bash

java -javaagent:/home/yayu/Downloads/jacoco/lib/jacocoagent.jar=includes=*,output=tcpserver -jar target/distributedfuzzingengine-1.0-SNAPSHOT-shaded.jar
