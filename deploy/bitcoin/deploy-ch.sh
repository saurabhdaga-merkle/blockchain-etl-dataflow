#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhousePipeline \
  -Dexec.args="--currency=bitcoin \
--pubSubSubcriptionPrefix=crypto_bitcoin.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-btc-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhouse0LagPipeline \
  -Dexec.args="--currency=bitcoin \
--pubSubSubcriptionPrefix=crypto_bitcoin_0_lag.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-btc-prod-0-lag--06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
