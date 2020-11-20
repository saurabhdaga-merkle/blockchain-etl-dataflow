package io.blockchainetl.ripple.clickhouse;

import org.apache.beam.sdk.schemas.Schema;
import org.joda.time.DateTime;

import static org.apache.beam.sdk.schemas.Schema.of;

public class Schemas {
    public static final Schema Payments = of(
            Schema.Field.of("hash", Schema.FieldType.STRING),
            Schema.Field.of("executed_time", Schema.FieldType.DATETIME.withNullable(true)),
            Schema.Field.of("account", Schema.FieldType.STRING),
            Schema.Field.of("destination", Schema.FieldType.STRING),
            Schema.Field.of("ledger_index", Schema.FieldType.INT64.withNullable(true)),
            Schema.Field.of("delivered_amount", Schema.FieldType.DOUBLE.withNullable(true)),
            Schema.Field.of("fee", Schema.FieldType.DOUBLE),
            Schema.Field.of("source_tag", Schema.FieldType.INT64.withNullable(true)),
            Schema.Field.of("destination_tag", Schema.FieldType.INT64.withNullable(true))
    );

    public static final Schema Reports = of(
            Schema.Field.of("hash", Schema.FieldType.STRING),
            Schema.Field.of("executed_time", Schema.FieldType.DATETIME.withNullable(true)),
            Schema.Field.of("ledger_index", Schema.FieldType.INT64.withNullable(true)),
            Schema.Field.of("account", Schema.FieldType.STRING),
            Schema.Field.of("amount", Schema.FieldType.DOUBLE)
    );

    private Schemas() {
    }
}
