#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinHotPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoin0Lag.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=bitcoin-0-lag-prod-4 \
--workerMachineType=n1-standard-1 \
--numWorkers=4 \
--maxNumWorkers=4 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
