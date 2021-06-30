#!/usr/bin/env bash

python3 delete_dataflow.py tigergraph-ripple

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ripple.RipplePubSubToTigerGraphPipeline \
  -Dexec.args="--currency=ripple \
--tigergraphHost=http://m2.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_ripple_tigergraph_m2.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ripple-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ripple-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-ripple-m2-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ripple.RipplePubSubToTigerGraphPipeline \
  -Dexec.args="--currency=ripple \
--tigergraphHost=http://m3.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_ripple_tigergraph_m3.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ripple-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ripple-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-ripple-m3-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
