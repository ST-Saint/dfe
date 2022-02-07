#!/usr/bin/env bash

java -jar /home/yayu/Downloads/jacoco/lib/jacococli.jar report jacoco.exec --classfiles ./target/classes/ --html jacoco/
