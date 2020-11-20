#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.LitecoinHotPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigLitecoin0Lag.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=litecoin-0-lag-prod \
--workerMachineType=n1-standard-1 \
--numWorkers=2 \
--maxNumWorkers=2 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
