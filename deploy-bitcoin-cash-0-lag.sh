#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinCashHotPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCash0Lag.json \
--allowedTimestampSkewSeconds=1000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-etl/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-etl/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bitcoin-cash-0-lag-prod-1238 \
--workerMachineType=n1-standard-1 \
--numWorkers=4 \
--maxNumWorkers=4 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
