#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphMempoolPipeline \
  -Dexec.args="--currency=bitcoin \
--tigergraphHost=http://m1.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_mempool_tigergraph_m1.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl-mempool-dev/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl-mempool-dev/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-mempool-m1-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphMempoolPipeline \
  -Dexec.args="--currency=bitcoin \
--tigergraphHost=http://m3.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_mempool_tigergraph_m3.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-etl-mempool-dev/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-etl-mempool-dev/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-mempool-m3-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"