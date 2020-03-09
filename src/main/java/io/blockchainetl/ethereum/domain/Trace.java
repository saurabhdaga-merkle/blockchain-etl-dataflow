package io.blockchainetl.ethereum.domain;

import com.google.common.base.Objects;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

@DefaultCoder(AvroCoder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trace {

    @Nullable
    @JsonProperty("transaction_hash")
    private String transactionHash;

    @Nullable
    @JsonProperty("transaction_index")
    private Long transactionIndex;

    @Nullable
    @JsonProperty("from_address")
    private String fromAddress;

    @Nullable
    @JsonProperty("to_address")
    private String toAddress;

    @Nullable
    private BigInteger value;

    @Nullable
    private String input;

    @Nullable
    private String output;

    @Nullable
    @JsonProperty("trace_type")
    private String traceType;

    @Nullable
    @JsonProperty("call_type")
    private String callType;

    @Nullable
    @JsonProperty("reward_type")
    private String rewardType;

    @Nullable
    private Long gas;

    @Nullable
    @JsonProperty("gas_used")
    private Long gasUsed;

    @Nullable
    private Long subtraces;

    @Nullable
    @JsonProperty("trace_address")
    private List<Long> traceAddress;

    @Nullable
    private String error;

    @Nullable
    private Integer status;

    @Nullable
    @JsonProperty("block_timestamp")
    private Long blockTimestamp;

    @Nullable
    @JsonProperty("block_number")
    private Long blockNumber;

    @Nullable
    @JsonProperty("block_hash")
    private String blockHash;

    public Trace() {
    }

    public String getTransactionHash() {
        if (transactionHash == null)
            return "0x";
        return transactionHash;
    }

    public String getFromAddress() {
        if (fromAddress == null)
            return "0x";
        return fromAddress;
    }

    public String getToAddress() {
        if (toAddress == null)
            return "0x";
        return toAddress;
    }

    public String getValue() {
        return value.toString();
    }

    public Long getGas() {
        if (gas == null)
            return 0L;
        return gas;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public DateTime getBlockDateTime() {
        return new DateTime(blockTimestamp * 1000);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trace trace = (Trace) o;
        return Objects.equal(transactionHash, trace.transactionHash) &&
                Objects.equal(transactionIndex, trace.transactionIndex) &&
                Objects.equal(fromAddress, trace.fromAddress) &&
                Objects.equal(toAddress, trace.toAddress) &&
                Objects.equal(value, trace.value) &&
                Objects.equal(input, trace.input) &&
                Objects.equal(output, trace.output) &&
                Objects.equal(traceType, trace.traceType) &&
                Objects.equal(callType, trace.callType) &&
                Objects.equal(rewardType, trace.rewardType) &&
                Objects.equal(gas, trace.gas) &&
                Objects.equal(gasUsed, trace.gasUsed) &&
                Objects.equal(subtraces, trace.subtraces) &&
                Objects.equal(traceAddress, trace.traceAddress) &&
                Objects.equal(error, trace.error) &&
                Objects.equal(status, trace.status) &&
                Objects.equal(blockTimestamp, trace.blockTimestamp) &&
                Objects.equal(blockNumber, trace.blockNumber) &&
                Objects.equal(blockHash, trace.blockHash);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionHash, transactionIndex, fromAddress, toAddress, value, input, output,
                traceType,
                callType, rewardType, gas, gasUsed, subtraces, traceAddress, error, status, blockTimestamp, blockNumber,
                blockHash);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("transactionHash", transactionHash)
                .add("transactionIndex", transactionIndex)
                .add("fromAddress", fromAddress)
                .add("toAddress", toAddress)
                .add("value", value)
                .add("input", input)
                .add("output", output)
                .add("traceType", traceType)
                .add("callType", callType)
                .add("rewardType", rewardType)
                .add("gas", gas)
                .add("gasUsed", gasUsed)
                .add("subtraces", subtraces)
                .add("traceAddress", traceAddress)
                .add("error", error)
                .add("status", status)
                .add("blockTimestamp", blockTimestamp)
                .add("blockNumber", blockNumber)
                .add("blockHash", blockHash)
                .toString();
    }
}
