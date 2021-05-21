#!/usr/bin/env bash

mvn -Pdirect-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.LitecoinPubSubToTigerGraphKafkaPipeline \
  -Dexec.args="--chainConfigFile=chainConfigLitecoinCoinTaint0LagM1.json --allowedTimestampSkewSeconds=36000 --defaultSdkHarnessLogLevel=DEBUG \
--tempLocation=gs://your-bucket/dataflow" -e -X