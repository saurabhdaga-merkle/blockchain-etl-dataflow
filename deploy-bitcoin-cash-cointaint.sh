#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.BitcoinCashPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigBitcoinCashCointaint.json \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=bitcoin-cash-cointaint-tigergraph-1 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
