#!/usr/bin/env bash

python3 delete_dataflow.py clickhouse-ethereum

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumPubSubToClickhousePipeline \
  -Dexec.args="--currency=ethereum \
--pubSubSubcriptionPrefix=crypto_ethereum.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ethereum-etl/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ethereum-etl/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-eth-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=2 \
--maxNumWorkers=2 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"

#!/usr/bin/env bash

mvn -e -Pdataflow-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumHotPubSubToClickhousePipeline \
  -Dexec.args="--currency=ethereum \
--pubSubSubcriptionPrefix=crypto_ethereum_0_lag.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
--gcpTempLocation=gs://blockchain-etl-streaming/ethereum-etl/hot/dataflow \
--tempLocation=gs:///blockchain-etl-streaming/ethereum-etl/hot/dataflow \
--project=staging-btc-etl \
--runner=DataflowRunner \
--jobName=clickhouse-eth-0-lag-prod-06-28 \
--workerMachineType=n1-standard-1 \
--numWorkers=2 \
--maxNumWorkers=2 \
--diskSizeGb=30 \
--region=us-central1 \
--zone=us-central1-a \
"
