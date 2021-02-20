#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinCashPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCash.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=bitcoin-cash-prod-1 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
