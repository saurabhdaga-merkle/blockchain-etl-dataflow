#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ripple.RipplePubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigRippleCointaintM2.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ripple-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ripple-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-ripple-m2-11 \
--workerMachineType=n1-standard-1 \
--numWorkers=6 \
--maxNumWorkers=6 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ripple.RipplePubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigRippleCointaintM3.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ripple-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ripple-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-ripple-cointaint-m3-11 \
--workerMachineType=n1-standard-1 \
--numWorkers=6 \
--maxNumWorkers=6 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
