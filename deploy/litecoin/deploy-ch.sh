#!/usr/bin/env bash

python3 delete_dataflow.py clickhouse-ltc

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhousePipeline \
  -Dexec.args="--currency=litecoin \
--pubSubSubcriptionPrefix=crypto_litecoin.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-litecoin-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhouse0LagPipeline \
  -Dexec.args="--currency=litecoin \
--pubSubSubcriptionPrefix=crypto_litecoin_0_lag.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-litecoin-0-lag-prod-06-28-1 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-b \
"

