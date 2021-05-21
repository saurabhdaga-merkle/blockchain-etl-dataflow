#!/usr/bin/env bash

mvn -Pdirect-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumHotPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigEthereum0LagCointaintM2.json --allowedTimestampSkewSeconds=36000 --defaultSdkHarnessLogLevel=DEBUG \
--tempLocation=gs://your-bucket/dataflow"  -e -X
