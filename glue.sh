#!/bin/bash
user=`whoami`
export M2_REPO=/home/$user/.m2/repository
export NABU_REPO=$M2_REPO/be/nabu

export CLASSPATH=$NABU_REPO/libs/converter/converter-api/1.0-SNAPSHOT/converter-api-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/libs/converter/converter-base/1.0-SNAPSHOT/converter-base-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/libs/evaluator/evaluator-api/1.0-SNAPSHOT/evaluator-api-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/glue/glue/1.0-SNAPSHOT/glue-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:$NABU_REPO/glue/glue-selenese/1.0-SNAPSHOT/glue-selenese-1.0-SNAPSHOT.jar

java -cp $CLASSPATH be.nabu.glue.Main "$@"

