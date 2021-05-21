#!/usr/bin/env bash

mvn -Pdirect-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ripple.RipplePubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigRippleCointaintM3.json --allowedTimestampSkewSeconds=36000 --defaultSdkHarnessLogLevel=DEBUG \
--tempLocation=gs://your-bucket/dataflow"  -e -X
