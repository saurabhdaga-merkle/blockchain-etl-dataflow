package io.blockchainetl.bitcoin.clickhouse;

import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.Field;

import static org.apache.beam.sdk.schemas.Schema.FieldType;
import static org.apache.beam.sdk.schemas.Schema.of;

public final class Schemas {
    public static final Schema TRANSACTIONS = of(
            Field.of("transaction_id", FieldType.STRING),
            Field.of("hash", FieldType.STRING),
            Field.of("block_number", FieldType.INT64),
            Field.of("block_hash", FieldType.STRING),
            Field.of("block_date_time", FieldType.DATETIME),
            Field.of("is_coinbase", FieldType.INT16),
            Field.of("lock_time", FieldType.INT64),
            Field.of("size", FieldType.INT64.withNullable(true)),
            Field.of("virtual_size", FieldType.INT64.withNullable(true)),
            Field.of("weight", FieldType.INT64.withNullable(true)),
            Field.of("version", FieldType.INT64),
            Field.of("input_count", FieldType.INT64),
            Field.of("output_count", FieldType.INT64),
            Field.of("input_value", FieldType.INT64.withNullable(true)),
            Field.of("output_value", FieldType.INT64.withNullable(true)),
            Field.of("fee", FieldType.INT64.withNullable(true)),
            Field.of("coin_price_usd", FieldType.FLOAT),
            Field.of("inputs.value", FieldType.array(FieldType.INT64)),
            Field.of("inputs.type", FieldType.array(FieldType.STRING)),
            Field.of("inputs.required_signatures", FieldType.array(FieldType.INT16)),
            Field.of("inputs.index", FieldType.array(FieldType.INT64)),
            Field.of("inputs.create_transaction_id", FieldType.array(FieldType.STRING)),
            Field.of("inputs.spending_transaction_id", FieldType.array(FieldType.STRING)),
            Field.of("inputs.create_output_index", FieldType.array(FieldType.INT64)),
            Field.of("inputs.script_asm", FieldType.array(FieldType.STRING)),
            Field.of("inputs.script_hex", FieldType.array(FieldType.STRING)),
            Field.of("inputs.addresses", FieldType.array(FieldType.STRING)),
            Field.of("outputs.value", FieldType.array(FieldType.INT64)),
            Field.of("outputs.type", FieldType.array(FieldType.STRING)),
            Field.of("outputs.required_signatures", FieldType.array(FieldType.INT16)),
            Field.of("outputs.create_transaction_id", FieldType.array(FieldType.STRING)),
            Field.of("outputs.index", FieldType.array(FieldType.INT64)),
            Field.of("outputs.script_asm", FieldType.array(FieldType.STRING)),
            Field.of("outputs.script_hex", FieldType.array(FieldType.STRING)),
            Field.of("outputs.addresses", FieldType.array(FieldType.STRING)));

    public static final Schema TRANSACTIONS_HOT = of(
            Field.of("transaction_id", FieldType.STRING),
            Field.of("hash", FieldType.STRING),
            Field.of("block_number", FieldType.INT64),
            Field.of("block_hash", FieldType.STRING),
            Field.of("block_date_time", FieldType.DATETIME),
            Field.of("is_coinbase", FieldType.INT16),
            Field.of("lock_time", FieldType.INT64),
            Field.of("size", FieldType.INT64.withNullable(true)),
            Field.of("virtual_size", FieldType.INT64.withNullable(true)),
            Field.of("weight", FieldType.INT64.withNullable(true)),
            Field.of("version", FieldType.INT64),
            Field.of("input_count", FieldType.INT64),
            Field.of("output_count", FieldType.INT64),
            Field.of("input_value", FieldType.INT64.withNullable(true)),
            Field.of("output_value", FieldType.INT64.withNullable(true)),
            Field.of("fee", FieldType.INT64.withNullable(true)),
            Field.of("coin_price_usd", FieldType.FLOAT),
            Field.of("inputs.value", FieldType.array(FieldType.INT64)),
            Field.of("inputs.type", FieldType.array(FieldType.STRING)),
            Field.of("inputs.required_signatures", FieldType.array(FieldType.INT16)),
            Field.of("inputs.index", FieldType.array(FieldType.INT64)),
            Field.of("inputs.create_transaction_id", FieldType.array(FieldType.STRING)),
            Field.of("inputs.spending_transaction_id", FieldType.array(FieldType.STRING)),
            Field.of("inputs.create_output_index", FieldType.array(FieldType.INT64)),
            Field.of("inputs.script_asm", FieldType.array(FieldType.STRING)),
            Field.of("inputs.script_hex", FieldType.array(FieldType.STRING)),
            Field.of("inputs.addresses", FieldType.array(FieldType.STRING)),
            Field.of("outputs.value", FieldType.array(FieldType.INT64)),
            Field.of("outputs.type", FieldType.array(FieldType.STRING)),
            Field.of("outputs.required_signatures", FieldType.array(FieldType.INT16)),
            Field.of("outputs.create_transaction_id", FieldType.array(FieldType.STRING)),
            Field.of("outputs.index", FieldType.array(FieldType.INT64)),
            Field.of("outputs.script_asm", FieldType.array(FieldType.STRING)),
            Field.of("outputs.script_hex", FieldType.array(FieldType.STRING)),
            Field.of("outputs.addresses", FieldType.array(FieldType.STRING)),
            Field.of("sign", FieldType.BYTE));

    public static final Schema BLOCKS = of(
            Field.of("hash", FieldType.STRING),
            Field.of("number", FieldType.INT64),
            Field.of("date_time", FieldType.DATETIME),
            Field.of("median_timestamp", FieldType.INT64),
            Field.of("merkle_root", FieldType.STRING),
            Field.of("coinbase_param", FieldType.STRING),
            Field.of("coinbase_param_decoded", FieldType.STRING),
            Field.of("coinbase_txid", FieldType.STRING),
            Field.of("nonce", FieldType.STRING),
            Field.of("difficulty", FieldType.INT64),
            Field.of("chain_work", FieldType.STRING),
            Field.of("version", FieldType.INT64),
            Field.of("version_hex", FieldType.STRING),
            Field.of("size", FieldType.INT64),
            Field.of("stripped_size", FieldType.INT64.withNullable(true)),
            Field.of("weight", FieldType.INT64.withNullable(true)),
            Field.of("bits", FieldType.STRING),
            Field.of("block_reward", FieldType.INT64),
            Field.of("coin_price_usd", FieldType.FLOAT));


    private Schemas() {
    }
}
