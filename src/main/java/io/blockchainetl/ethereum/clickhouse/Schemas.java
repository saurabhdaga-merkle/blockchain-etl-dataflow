package io.blockchainetl.ethereum.clickhouse;

import org.apache.beam.sdk.schemas.Schema;

/*
CREATE TABLE ethereum.ethereum_master (
        transaction_hash String,
        sender_address String,
        receiver_address String,
        type UInt8,
        value Decimal128(8),
        token_address String,
        block_date_time DateTime,
        sender_cluster Nullable(UInt64),
        receiver_cluster Nullable(UInt64),
        fee Decimal128(8),
        gas Int64,
        log_index Int32,
        uuid UUID,
        sign Int8
        )
        ENGINE = ReplacingMergeTree()
        PARTITION BY toYYYYMM(block_date_time)
        ORDER BY (transaction_hash, type, gas, log_index, uuid)
        SETTINGS index_granularity=2096

 */

public final class Schemas {
    public static final Schema MASTER = Schema.of(
            Schema.Field.of("transaction_hash", Schema.FieldType.STRING),
            Schema.Field.of("sender_address", Schema.FieldType.STRING),
            Schema.Field.of("receiver_address", Schema.FieldType.STRING),
            Schema.Field.of("type", Schema.FieldType.INT16),
            Schema.Field.of("value", Schema.FieldType.DECIMAL),
            Schema.Field.of("token_address", Schema.FieldType.STRING),
            Schema.Field.of("block_date_time", Schema.FieldType.DATETIME),
            Schema.Field.of("sender_cluster", Schema.FieldType.INT64.withNullable(true)),
            Schema.Field.of("receiver_cluster", Schema.FieldType.INT64.withNullable(true)),
            Schema.Field.of("fee", Schema.FieldType.DECIMAL),
            Schema.Field.of("gas", Schema.FieldType.INT64),
            Schema.Field.of("log_index", Schema.FieldType.INT32)
    );

    private Schemas() {
    }
}
