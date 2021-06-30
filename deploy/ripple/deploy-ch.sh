#!/usr/bin/env bash

python3 delete_dataflow.py clickhouse-ripple

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ripple.RipplePubSubToClickhousePipeline \
  -Dexec.args="--currency=ripple \
--pubSubSubcriptionPrefix=crypto_ripple.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ripple-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ripple-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-ripple-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
