#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCointaint0LagM1.json \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-0-lag-m1-10  \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=8 \
--numWorkers=8 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCointaint0LagM3.json \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-0-lag-m3-10 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=8 \
--numWorkers=8 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
