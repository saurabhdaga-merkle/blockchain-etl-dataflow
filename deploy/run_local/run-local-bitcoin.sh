#!/usr/bin/env bash

mvn -Pdirect-runner compile exec:java \
  -Dexec.mainClass=io.blockchainetl.bitcoin.PubSubToTigerGraphMempoolPipeline \
  -Dexec.args="--currency=litecoin \
--tigergraphHost=http://18.116.135.9:9000 \
--pubSubSubcriptionPrefix=crypto_litecoin_mempool.dataflow.clickhouse.
--allowedTimestampSkewSeconds=36000 \
  --tempLocation=gs://your-bucket/dataflow" -e -X
