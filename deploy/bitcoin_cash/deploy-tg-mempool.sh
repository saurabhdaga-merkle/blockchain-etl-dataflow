#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphMempoolPipeline \
  -Dexec.args="--currency=bitcoin_cash \
--tigergraphHost=http://m1.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_mempool_m1.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin_cash-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin_cash-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bch-mempool-m1-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"


mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphMempoolPipeline \
  -Dexec.args="--currency=bitcoin_cash \
--tigergraphHost=http://m2.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_mempool_m2.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin_cash-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin_cash-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bch-mempool-m2-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
