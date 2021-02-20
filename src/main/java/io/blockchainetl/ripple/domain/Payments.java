package io.blockchainetl.ripple.domain;


import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import java.io.Serializable;

@DefaultCoder(AvroCoder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payments implements Serializable {
    @Nullable
    private String hash;
    @Nullable
    @JsonProperty("executed_time")
    private DateTime executedTime;
    @Nullable
    private String account;
    @Nullable
    private String destination;
    @Nullable
    @JsonProperty("ledger_index")
    private Long ledger_index;
    @Nullable
    @JsonProperty("delivered_amount")
    private Double delivered_amount;
    @Nullable
    private Double fee;
    @Nullable
    @JsonProperty("source_tag")
    private Long source_tag;
    @Nullable
    @JsonProperty("destination_tag")
    private Long destination_tag;

    public Payments() {
    }

    @Override
    public String toString() {
        return "Payments{" +
                "hash='" + hash + '\'' +
                ", executedTime='" + executedTime + '\'' +
                ", account='" + account + '\'' +
                ", destination='" + destination + '\'' +
                ", ledger_index=" + ledger_index +
                ", delivered_amount='" + delivered_amount + '\'' +
                ", fee='" + fee + '\'' +
                ", source_tag=" + source_tag +
                ", destination_tag=" + destination_tag +
                '}';
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String tx_hash) {
        this.hash = tx_hash;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Long getLedgerIndex() {
        return ledger_index;
    }

    public void setLedgerIndex(Long ledger_index) {
        this.ledger_index = ledger_index;
    }

    public Double getDeliveredAmount() {
        return delivered_amount;
    }

    public void setDeliveredAmount(Double delivered_amount) {
        this.delivered_amount = delivered_amount;
    }

    public Long getSourceTag() {
        return source_tag;
    }

    public void setSourceTag(Long source_tag) {
        this.source_tag = source_tag;
    }

    public Long getDestinationTag() {
        return destination_tag;
    }

    public void setDestinationTag(Long destination_tag) {
        this.destination_tag = destination_tag;
    }

    public DateTime getExecutedTime() {
        return executedTime;
    }

    public void setExecutedTime(DateTime executedTime) {
        this.executedTime = executedTime;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }
}
