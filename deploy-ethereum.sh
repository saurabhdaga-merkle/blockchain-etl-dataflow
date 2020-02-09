#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumPubSubToClickhousePipeline \
  -Dexec.args="--chainConfigFile=chainConfigEthereum.json \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ethereum-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ethereum-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=ethereum-pubsub-to-clickhouse-dev \
--workerMachineType=n1-standard-1 \
--maxNumWorkers=1 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
