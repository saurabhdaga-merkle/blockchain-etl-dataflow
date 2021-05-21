#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinCashSVHotPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCashSV0Lag.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-sv-etl/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-sv-etl/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bitcoin-cash-sv-0-lag-prod-1238 \
--workerMachineType=n1-standard-2 \
--numWorkers=2 \
--maxNumWorkers=2 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
