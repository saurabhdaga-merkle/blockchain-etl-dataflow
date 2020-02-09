package io.blockchainetl.bitcoin.domain;

import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

@DefaultCoder(AvroCoder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block {

    @Nullable
    private String type;

    @Nullable
    private String hash;

    @Nullable
    private Long number;

    @Nullable
    @JsonProperty("timestamp")
    private Long timestamp;

    @Nullable
    @JsonProperty("median_timestamp")
    private Long medianTimestamp;

    @Nullable
    @JsonProperty("merkle_root")
    private String merkleRoot;

    @Nullable
    @JsonProperty("coinbase_param")
    private String coinbaseParam;

    @Nullable
    @JsonProperty("coinbase_param_decoded")
    private String coinbaseParamDecoded;

    @Nullable
    @JsonProperty("coinbase_txid")
    private String coinbaseTxid;

    @Nullable
    private String nonce;

    @Nullable
    private Long difficulty;

    @Nullable
    @JsonProperty("chain_work")
    private String chainWork;

    @Nullable
    private Long version;

    @Nullable
    @JsonProperty("version_hex")
    private String versionHex;

    @Nullable
    private Long size;

    @Nullable
    @JsonProperty("stripped_size")
    private Long strippedSize;

    @Nullable
    private Long weight;

    @Nullable
    private String bits;

    @Nullable
    @JsonProperty("block_reward")
    private Long blockReward;

    @Nullable
    @JsonProperty("coin_price_usd")
    private Float coinPriceUSD;


    public Block() {
    }

    public String getHash() {
        return hash;
    }

    public Long getNumber() {
        return number;
    }

    public DateTime getDateTime() {
        return new DateTime(timestamp);
    }

    public Long getMedianTimestamp() {
        return medianTimestamp;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public String getCoinbaseParam() {
        return coinbaseParam;
    }

    public String getCoinbaseParamDecoded() {
        return coinbaseParamDecoded;
    }

    public String getCoinbaseTxid() {
        return coinbaseTxid;
    }

    public String getNonce() {
        return nonce;
    }

    public Long getDifficulty() {
        return difficulty;
    }

    public String getChainWork() {
        return chainWork;
    }

    public Long getVersion() {
        return version;
    }

    public String getVersionHex() {
        return versionHex;
    }

    public Long getSize() {
        return size;
    }

    public Long getStrippedSize() {
        return strippedSize;
    }

    public Long getWeight() {
        return weight;
    }

    public String getBits() {
        return bits;
    }

    public Long getBlockReward() {
        return blockReward;
    }

    public Float getCoinPriceUSD() {
        return coinPriceUSD;
    }

}
