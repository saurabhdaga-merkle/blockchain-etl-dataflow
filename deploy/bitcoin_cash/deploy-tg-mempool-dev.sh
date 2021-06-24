#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphMempoolPipeline \
  -Dexec.args="--currency=bitcoin_cash \
--tigergraphHost=http://m1.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_mempool_m1.dataflow.clickhouse. \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin_cash-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin_cash-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-cash-mempool-m1-06-23 \
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
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin_cash-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin_cash-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-cash-mempool-m2-06-23 \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphPipeline \
  -Dexec.args="--currency=bitcoin_cash \
--tigergraphHost=http://18.116.135.9:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_tigergraph_dev.dataflow.clickhouse. \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/litecoin-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-cash-n-lag-dev \
--workerMachineType=n1-standard-1 \
--numWorkers=1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
