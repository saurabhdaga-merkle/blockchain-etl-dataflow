#!/usr/bin/env bash

mvn -Pdirect-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.ethereum.EthereumPubSubToClickhousePipeline \
  -Dexec.args="--currency=ethereum \
--pubSubSubcriptionPrefix=crypto_ethereum_mempool.dataflow.clickhouse. \
--allowedTimestampSkewSeconds=36000 \
 --tempLocation=gs://your-bucket/dataflow" \
"