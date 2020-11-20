#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.LitecoinPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigLitecoinCoinTaint.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=litecoin-tigergraph-dev-5 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=4 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
