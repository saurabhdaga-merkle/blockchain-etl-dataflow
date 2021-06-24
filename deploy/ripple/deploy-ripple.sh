#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ripple.RipplePubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigRipple.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ripple-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ripple-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-ripple-prod-1238 \
--workerMachineType=n1-standard-1 \
--numWorkers=3 \
--maxNumWorkers=3 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
