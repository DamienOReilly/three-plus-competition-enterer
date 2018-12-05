#!/usr/bin/env sh

set -e

command -v sbt >/dev/null 2>&1 || { echo >&2 "sbt not found."; exit 1; }
command -v git >/dev/null 2>&1 || { echo >&2 "git not found."; exit 1; }
command -v docker >/dev/null 2>&1 || { echo >&2 "docker not found."; exit 1; }

BINARY_NAME=threeplus
GRAALVM_CMD="cd /tmp && native-image --no-server --verbose -H:+ReportUnsupportedElementsAtRuntime -H:IncludeResources=application.conf -H:IncludeResources=logback.xml -H:Log=registerResource: --enable-https -jar $PWD/app/target/scala-2.12/ThreePlusCompetitionApp-assembly-*.jar -H:Name=$BINARY_NAME && cp /opt/graalvm-ce-*/jre/lib/amd64/libsunec.so $PWD/target && cp $BINARY_NAME $PWD/target && strip $PWD/target/$BINARY_NAME && strip $PWD/target/libsunec.so"
GRAALVM_VERSION=1.0.0-rc9

NAME="damo2k/threepluscompetitionenterer"
TAG=$(git log -1 --pretty=%h)
IMG="$NAME:$TAG"
LATEST="$NAME:latest"

sbt clean compile test assembly

if [ $? -ne 0 ]
then
   echo "Failed to build fat jar."
   exit 1
fi

docker run \
  --rm=true \
  -it \
  --user=root \
  --entrypoint=bash \
  -v $PWD:$PWD \
  oracle/graalvm-ce:$GRAALVM_VERSION \
  -c "$GRAALVM_CMD"

if [ $? -ne 0 ]
then
   echo "Failed to create GraalVM native binary."
   exit 1
fi

docker build -t $IMG . && docker tag $IMG $LATEST && docker push $NAME
