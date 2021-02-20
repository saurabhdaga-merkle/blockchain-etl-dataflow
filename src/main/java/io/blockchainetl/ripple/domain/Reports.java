package io.blockchainetl.ripple.domain;

import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

@DefaultCoder(AvroCoder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reports {

    @Nullable
    @JsonProperty("account")
    private String account;
    @Nullable
    @JsonProperty("amount")
    private Double amount;
    @Nullable
    @JsonProperty("hash")
    private String hash;
    @Nullable
    @JsonProperty("executed_time")
    private DateTime executedTime;
    @Nullable
    @JsonProperty("ledger_index")
    private Long ledger_index;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public DateTime getExecutedTime() {
        return executedTime;
    }

    public void setExecutedTime(DateTime executedTime) {
        this.executedTime = executedTime;
    }

    public Long getLedger_index() {
        return ledger_index;
    }

    public void setLedger_index(Long ledger_index) {
        this.ledger_index = ledger_index;
    }
}
