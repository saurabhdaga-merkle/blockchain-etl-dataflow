#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphPipeline \
  -Dexec.args="--currency=bitcoin_cash \
--tigergraphHost=http://m1.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_tigergraph_m1.dataflow.clickhouse. \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-cash-m1-06-23 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=6 \
--numWorkers=6 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"




mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphPipeline \
  -Dexec.args="--currency=bitcoin_cash \
--tigergraphHost=http://m2.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_bitcoin_cash_tigergraph_m2.dataflow.clickhouse. \
--defaultSdkHarnessLogLevel=DEBUG \
--gcpTempLocation=gs://blockchain-etl-streaming/bitcoin-cash-etl-cointaint/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/bitcoin-cash-etl-cointaint/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-bitcoin-cash-m2-06-23 \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=6 \
--numWorkers=6 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
