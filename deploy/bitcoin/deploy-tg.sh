#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCointaintM1.json \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=prod-tigergraph-bitcoin-m1-06-14-1 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCointaintM3.json \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=prod-tigergraph-bitcoin-m3-06-14-1 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
