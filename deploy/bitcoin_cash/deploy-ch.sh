#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhousePipeline \
  -Dexec.args="--currency=bitcoin_cash \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin_cash-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin_cash-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bch-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhouse0LagPipeline \
  -Dexec.args="--currency=bitcoin_cash \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_0_lag.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin_cash-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin_cash-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bch-0-lag-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-b \
"

