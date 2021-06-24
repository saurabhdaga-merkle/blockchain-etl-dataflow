#!/usr/bin/env bash

mvn -Pdirect-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ripple.RipplePubSubToTigerGraphPipeline \
  -Dexec.args="-currency=litecoin \
--tigergraphHost=http://18.116.135.9:9000 \
--pubsub
  --allowedTimestampSkewSeconds=36000 --defaultSdkHarnessLogLevel=DEBUG \
--tempLocation=gs://your-bucket/dataflow"  -e -X
