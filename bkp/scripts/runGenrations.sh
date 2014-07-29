#!/bin/bash
BASE_DIR=$(pwd)

JOB_JAR_PATH="/home/tkepe/mestrado/master/ktt/self-tuning/jar/wordcount.jar"
JOB_CLASS="org.apache.hadoop.examples.WordCount"
JOB_INPUT="/kepe/in"
#JOB_INPUT="/kepe/sample/20%"

AUTOCONF_CMD="/home/tkepe/mestrado/ktt/AutoConf/bin/autoconfctl -j wordcount -f "

cd $BASE_DIR/results
for gen in $(ls .); do
    JOB_OUTPUT="/kepe/out/$gen"
    $AUTOCONF_CMD $gen -a
    CMD="hadoop jar $JOB_JAR_PATH $JOB_CLASS $JOB_INPUT $JOB_OUTPUT"
    { time $CMD ; } > $BASE_DIR/out/${gen}.time 2>&1
    echo "------------------------------------------------------"
done
