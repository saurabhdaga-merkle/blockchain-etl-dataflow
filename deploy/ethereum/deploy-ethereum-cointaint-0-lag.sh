#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumHotPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigEthereum0LagCointaintM2.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ethereum-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ethereum-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-ethereum-0-lag-m2-13 \
--workerMachineType=n1-standard-1 \
--numWorkers=4 \
--maxNumWorkers=4 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumHotPubSubToTigerGraphPipeline \
  -Dexec.args="--chainConfigFile=chainConfigEthereum0LagCointaintM3.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ethereum-etl-cointaint/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ethereum-etl-cointaint/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=tigergraph-ethereum-0-lag-m3-13 \
--workerMachineType=n1-standard-1 \
--numWorkers=4 \
--maxNumWorkers=4 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
