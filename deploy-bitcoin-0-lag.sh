#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinHotPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoin0Lag.json \
--allowedTimestampSkewSeconds=1000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bitcoin-0-lag-prod-1238 \
--workerMachineType=n1-standard-1 \
--numWorkers=6 \
--maxNumWorkers=6 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
