package io.blockchainetl.ethereum.clickhouse;

import org.apache.beam.sdk.schemas.Schema;

/*
┌─name─────────────┬─type───────────┬─default_type─┬─default_expression─┬─comment─┬─codec_expression─┬─ttl_expression─┐
│ transaction_hash │ String         │              │                    │         │                  │                │
│ sender_address   │ String         │              │                    │         │                  │                │
│ receiver_address │ String         │              │                    │         │                  │                │
│ type             │ UInt8          │              │                    │         │                  │                │
│ value            │ Decimal(38, 8) │              │                    │         │                  │                │
│ token_address    │ String         │              │                    │         │                  │                │
│ block_date_time  │ DateTime       │              │                    │         │                  │                │
│ gas              │ Int64          │              │                    │         │                  │                │
│ log_index        │ Int32          │              │                    │         │                  │                │
└──────────────────┴────────────
 */

public final class Schemas {
    public static final Schema MASTER = Schema.of(
            Schema.Field.of("transaction_hash", Schema.FieldType.STRING),
            Schema.Field.of("sender_address", Schema.FieldType.STRING),
            Schema.Field.of("receiver_address", Schema.FieldType.STRING),
            Schema.Field.of("type", Schema.FieldType.INT16),
            Schema.Field.of("value", Schema.FieldType.STRING),
            Schema.Field.of("token_address", Schema.FieldType.STRING),
            Schema.Field.of("block_date_time", Schema.FieldType.DATETIME),
            Schema.Field.of("fee", Schema.FieldType.STRING),
            Schema.Field.of("gas", Schema.FieldType.INT64),
            Schema.Field.of("log_index", Schema.FieldType.INT32),
            Schema.Field.of("status", Schema.FieldType.INT16),
            Schema.Field.of("uuid", Schema.FieldType.STRING)
    );

    private Schemas() {
    }
}
