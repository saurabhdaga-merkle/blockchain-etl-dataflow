package io.blockchainetl.bitcoin.domain;


import com.google.common.collect.Lists;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

@DefaultCoder(AvroCoder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements Serializable {

    @Nullable
    private String created_date;

    @Nullable
    private String type;

    @Nullable
    private String hash;

    @Nullable
    @JsonProperty("transaction_id")
    private String transactionId;

    @Nullable
    private Long size;

    @Nullable
    @JsonProperty("virtual_size")
    private Long virtualSize;

    @Nullable
    private Long version;

    @Nullable
    @JsonProperty("lock_time")
    private Long lockTime;

    @Nullable
    @JsonProperty("block_number")
    private Integer blockNumber;

    @Nullable
    @JsonProperty("block_hash")
    private String blockHash;

    @Nullable
    @JsonProperty("block_timestamp")
    private Long blockTimestamp;

    @Nullable
    @JsonProperty("is_coinbase")
    private Boolean isCoinbase;

    @Nullable
    @JsonProperty("weight")
    private Long weight;

    @Nullable
    @JsonProperty("input_count")
    private Long inputCount;

    @Nullable
    @JsonProperty("output_count")
    private Long outputCount;

    @Nullable
    @JsonProperty("input_value")
    private Long inputValue;

    @Nullable
    @JsonProperty("output_value")
    private Long outputValue;

    @Nullable
    private Long fee;

    @Nullable
    @JsonProperty("coin_price_usd")
    private Float coinPriceUSD;

    @Nullable
    private List<TransactionInput> inputs;

    @Nullable
    private List<TransactionOutput> outputs;

    public Transaction() {
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public String getHash() {
        return hash;
    }

    public Long getSize() {
        return size;
    }

    public Long getVirtualSize() {
        return virtualSize;
    }

    public Long getVersion() {
        return version;
    }

    public Long getLockTime() {
        return lockTime;
    }

    public Integer getBlockNumber() {
        return blockNumber;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public DateTime getBlockDateTime() {
        return new DateTime(blockTimestamp * 1000);
    }

    public int getCoinbase() {
        if (isCoinbase)
            return 1;
        else return 0;
    }

    public Long getInputCount() {
        return inputCount;
    }

    public Long getOutputCount() {
        return outputCount;
    }

    public Long getInputValue() {
        return inputValue;
    }

    public Long getOutputValue() {
        return outputValue;
    }

    public Long getFee() {
        return fee;
    }


    public List<Long> getInputsValue() {
        List<Long> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getValue() == null)
                data.add(0L);
            else
                data.add(eachInput.getValue());
        }

        return data;
    }

    public List<String> getInputsType() {
        List<String> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getType() == null)
                data.add("");
            else
                data.add(eachInput.getType());
        }

        return data;
    }

    public List<Short> getInputsRequiredSignatures() {
        List<Short> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getRequiredSignatures() == null)
                data.add((short) 0);
            else
                data.add(eachInput.getRequiredSignatures().shortValue());
        }

        return data;
    }

    public List<Long> getInputsIndex() {
        List<Long> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getIndex() == null)
                data.add(0L);
            else
                data.add(eachInput.getIndex());
        }

        return data;
    }

    public List<String> getInputsCreateTransactionId() {
        List<String> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getCreateTransactionId() == null)
                data.add("");
            else
                data.add(eachInput.getCreateTransactionId());
        }

        return data;
    }

    public List<String> getInputsSpendingTransactionId() {
        List<String> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getSpendingTransactionId() == null)
                data.add("");
            else
                data.add(eachInput.getSpendingTransactionId());
        }

        return data;
    }

    public List<Long> getInputsCreateOutputIndex() {
        List<Long> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getCreateOutputIndex() == null)
                data.add(0L);
            else
                data.add(eachInput.getCreateOutputIndex());
        }

        return data;
    }

    public List<String> getInputsScriptAsm() {
        List<String> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getScriptAsm() == null)
                data.add("");
            else
                data.add(eachInput.getScriptAsm());
        }

        return data;
    }

    public List<String> getInputsScriptHex() {
        List<String> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getScriptHex() == null)
                data.add("");
            else
                data.add(eachInput.getScriptHex());
        }

        return data;
    }

    public List<String> getInputsAddress() {
        List<String> data = Lists.newArrayList();

        for (TransactionInput eachInput : getInputs()) {
            if (eachInput.getAddresses() == null)
                data.add("");
            else
                data.add(eachInput.getAddresses());
        }

        return data;
    }

    public List<Long> getOutputsValue() {
        List<Long> data = Lists.newArrayList();

        for (TransactionOutput eachOutput : getOutputs()) {
            if (eachOutput.getValue() == null)
                data.add(0L);
            else
                data.add(eachOutput.getValue());
        }

        return data;
    }

    public List<String> getOutputsType() {
        List<String> data = Lists.newArrayList();

        for (TransactionOutput eachOutput : getOutputs()) {
            if (eachOutput.getType() == null)
                data.add("");
            else
                data.add(eachOutput.getType());
        }

        return data;
    }


    public List<Short> getOutputsRequiredSignatures() {
        List<Short> data = Lists.newArrayList();

        for (TransactionOutput eachOutput : getOutputs()) {
            if (eachOutput.getRequiredSignatures() == null)
                data.add((short) 0);
            else
                data.add(eachOutput.getRequiredSignatures().shortValue());
        }

        return data;
    }

    public List<String> getOutputsCreateTransactionId() {
        List<String> data = Lists.newArrayList();

        for (TransactionOutput eachOutput : getOutputs()) {
            if (eachOutput.getCreateTransactionId() == null)
                data.add("");
            else
                data.add(eachOutput.getCreateTransactionId());
        }

        return data;
    }

    public List<Long> getOutputsIndex() {
        List<Long> data = Lists.newArrayList();

        for (TransactionOutput eachOutput : getOutputs()) {
            if (eachOutput.getIndex() == null)
                data.add(0L);
            else
                data.add(eachOutput.getIndex());
        }

        return data;
    }

    public List<String> getOutputsScriptAsm() {
        List<String> data = Lists.newArrayList();

        for (TransactionOutput eachOutput : getOutputs()) {
            if (eachOutput.getScriptAsm() == null)
                data.add("");
            else
                data.add(eachOutput.getScriptAsm());
        }

        return data;
    }

    public List<String> getOutputsScriptHex() {
        List<String> data = Lists.newArrayList();

        for (TransactionOutput eachOutput : getOutputs()) {
            if (eachOutput.getScriptHex() == null)
                data.add("");
            else
                data.add(eachOutput.getScriptHex());
        }

        return data;
    }

    public List<String> getOutputsAddress() {
        List<String> data = Lists.newArrayList();

        for (TransactionOutput eachOutput : getOutputs()) {
            if (eachOutput.getAddresses() == null)
                data.add("");
            else
                data.add(eachOutput.getAddresses());
        }

        return data;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getWeight() {
        return weight;
    }

    public Float getCoinPriceUSD() {
        return coinPriceUSD;
    }
}
