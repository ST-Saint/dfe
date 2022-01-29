#!/usr/bin/env bash

dependencies="-hdfs-project/hadoop-hdfs/target/dependency/*"

classpath=target/classes


java -cp "${classpath}" edu.purdue.dfe.FuzzingServer
