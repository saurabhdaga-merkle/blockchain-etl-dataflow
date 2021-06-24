#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoin.json \
--allowedTimestampSkewSeconds=1000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bitcoin-prod-1238 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
