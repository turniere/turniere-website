#!/bin/sh
mvn clean package
echo "Moving package to remote host"
rsync target/turniere-*.jar turniere@dev.turnie.re:turniere.jar
echo "rsync exited with $?"
