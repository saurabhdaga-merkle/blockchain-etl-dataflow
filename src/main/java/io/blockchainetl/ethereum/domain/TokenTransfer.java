package io.blockchainetl.ethereum.domain;

import com.google.common.base.Objects;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.math.BigInteger;

@DefaultCoder(AvroCoder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenTransfer {

    @Nullable
    @JsonProperty("token_address")
    private String tokenAddress;

    @Nullable
    @JsonProperty("from_address")
    private String fromAddress;

    @Nullable
    @JsonProperty("to_address")
    private String toAddress;

    @Nullable
    private BigInteger value;

    @Nullable
    @JsonProperty("transaction_hash")
    private String transactionHash;

    @Nullable
    @JsonProperty("log_index")
    private Integer logIndex;

    @Nullable
    @JsonProperty("block_timestamp")
    private Long blockTimestamp;

    @Nullable
    @JsonProperty("block_number")
    private Long blockNumber;

    @Nullable
    @JsonProperty("block_hash")
    private String blockHash;

    public TokenTransfer() {
    }

    public String getTokenAddress() {
        return tokenAddress;
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

    public Integer getLogIndex() {
        return logIndex;
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
        TokenTransfer that = (TokenTransfer) o;
        return Objects.equal(tokenAddress, that.tokenAddress) &&
                Objects.equal(fromAddress, that.fromAddress) &&
                Objects.equal(toAddress, that.toAddress) &&
                Objects.equal(value, that.value) &&
                Objects.equal(transactionHash, that.transactionHash) &&
                Objects.equal(logIndex, that.logIndex) &&
                Objects.equal(blockTimestamp, that.blockTimestamp) &&
                Objects.equal(blockNumber, that.blockNumber) &&
                Objects.equal(blockHash, that.blockHash);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tokenAddress, fromAddress, toAddress, value, transactionHash, logIndex, blockTimestamp,
                blockNumber, blockHash);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("tokenAddress", tokenAddress)
                .add("fromAddress", fromAddress)
                .add("toAddress", toAddress)
                .add("value", value)
                .add("transactionHash", transactionHash)
                .add("logIndex", logIndex)
                .add("blockTimestamp", blockTimestamp)
                .add("blockNumber", blockNumber)
                .add("blockHash", blockHash)
                .toString();
    }
}
