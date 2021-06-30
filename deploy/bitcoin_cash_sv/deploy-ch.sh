#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhousePipeline \
  -Dexec.args="--currency=bitcoin_cash_sv \
--tigergraphHost=http://18.116.135.9:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_sv.dataflow.clickhouse. \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-sv-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-sv-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bitcoincashsv-prod-1238 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhouse0LagPipeline \
  -Dexec.args="--currency=bitcoin_cash_sv \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_sv_0_lag.dataflow.clickhouse. \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-sv-etl/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-sv-etl/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bitcoincashsv-0-lag-prod-1238 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
