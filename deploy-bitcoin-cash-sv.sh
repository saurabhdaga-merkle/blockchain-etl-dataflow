#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinCashSVPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCashSV.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-sv-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-sv-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-bitcoin-cash-sv-prod-1238 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
