#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphMempoolPipeline \
  -Dexec.args="--currency=litecoin \
--tigergraphHost=http://m1.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_litecoin_mempool_m1.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-litecoin-mempool-m1-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"


mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphMempoolPipeline \
  -Dexec.args="--currency=litecoin \
--tigergraphHost=http://m2.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_litecoin_mempool_m2.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-litecoin-mempool-m2-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
