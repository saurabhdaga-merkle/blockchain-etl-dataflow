#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinCashPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCashCointaint0LagM1.json \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-cash-0-lag-m1-9 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=10 \
--numWorkers=10 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"




mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinCashPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCashCointaint0LagM2.json \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-cash-0-lag-m2-9 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=10 \
--numWorkers=10 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
