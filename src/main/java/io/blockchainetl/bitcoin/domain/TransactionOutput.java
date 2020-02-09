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
public class TransactionOutput {

    @Nullable
    private Long index;

    @Nullable
    @JsonProperty("script_asm")
    private String scriptAsm;

    @Nullable
    @JsonProperty("script_hex")
    private String scriptHex;

    @Nullable
    @JsonProperty("required_signatures")
    private Integer requiredSignatures;

    @Nullable
    private String type;

    @Nullable
    private List<String> addresses;

    @Nullable
    private Long value;


    @Nullable
    @JsonProperty("create_transaction_id")
    private String createTransactionId;

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
}
