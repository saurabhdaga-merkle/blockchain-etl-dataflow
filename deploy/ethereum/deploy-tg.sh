#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumHotPubSubToTigerGraphPipeline \
  -Dexec.args="--currency=ethereum \
--tigergraphHost=http://m2.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_ethereum_0_lag_tigergraph_m2.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ethereum-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ethereum-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-eth-0-lag-m2-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=2 \
--maxNumWorkers=8 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumHotPubSubToTigerGraphPipeline \
  -Dexec.args="--currency=ethereum \
--tigergraphHost=http://m3.tigergraph.palantree.com:9000 \
--pubSubSubcriptionPrefix=crypto_ethereum_0_lag_tigergraph_m3.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ethereum-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ethereum-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-eth-0-lag-m3-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=2 \
--maxNumWorkers=8 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
