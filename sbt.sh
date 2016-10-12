#!/bin/bash

SBT_VERSION="0.13.12"

SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled"
[ ! -f 'sbt-launch.jar' ] && curl -o sbt-launch.jar -L "https://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/${SBT_VERSION}/sbt-launch.jar"
java $SBT_OPTS -jar `dirname $0`/sbt-launch.jar "$@"