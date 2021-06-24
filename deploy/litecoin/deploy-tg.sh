#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphPipeline \
  -Dexec.args="--currency=litecoin \
--tigergraphHost=http://m1.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_litecoin_tigergraph_m1.dataflow.clickhouse. \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-litecoin-m1-prod-06-24 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphPipeline \
  -Dexec.args="--currency=litecoin \
--tigergraphHost=http://m2.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_litecoin_tigergraph_m2.dataflow.clickhouse. \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-litecoin-m2-prod-06-24 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
