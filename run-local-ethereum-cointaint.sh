#!/usr/bin/env bash

mvn -Pdirect-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigEthereum.json --allowedTimestampSkewSeconds=36000 --defaultSdkHarnessLogLevel=DEBUG \
--tempLocation=gs://your-bucket/dataflow"  -e -X
