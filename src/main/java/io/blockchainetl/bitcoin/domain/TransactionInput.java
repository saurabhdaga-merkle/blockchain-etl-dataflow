package io.blockchainetl.bitcoin.domain;

import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@DefaultCoder(AvroCoder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionInput {

    @Nullable
    private Long index;

    @Nullable
    @JsonProperty("create_transaction_id")
    private String createTransactionId;

    @Nullable
    @JsonProperty("spending_transaction_id")
    private String spendingTransactionId;

    @Nullable
    @JsonProperty("create_output_index")
    private Long createOutputIndex;

    @Nullable
    @JsonProperty("script_asm")
    private String scriptAsm;

    @Nullable
    @JsonProperty("script_hex")
    private String scriptHex;

    @Nullable
    @JsonProperty("required_signatures")
    private int requiredSignatures;

    @Nullable
    private String type;

    @Nullable
    private List<String> addresses;

    @Nullable
    private Long value;

    public TransactionInput() {
    }

    public Long getIndex() {
        return index;
    }

    public String getScriptAsm() {
        return scriptAsm;
    }

    public String getScriptHex() {
        return scriptHex;
    }

    public Integer getRequiredSignatures() {
        return requiredSignatures;
    }


    public String getType() {
        return type;
    }


    public String getAddresses() {
        return StringUtils.join(addresses, ',');
    }

    public Long getValue() {
        return value;
    }

    public String getCreateTransactionId() {
        return createTransactionId;
    }

    public String getSpendingTransactionId() {
        return spendingTransactionId;
    }


    public Long getCreateOutputIndex() {
        return createOutputIndex;
    }
}
