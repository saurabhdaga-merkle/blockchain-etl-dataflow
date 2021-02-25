#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumHotPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigEthereum0Lag.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ethereum-etl/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ethereum-etl/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=ethereum-0-lag-prod \
--workerMachineType=n1-standard-1 \
--numWorkers=2 \
--maxNumWorkers=2 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
"