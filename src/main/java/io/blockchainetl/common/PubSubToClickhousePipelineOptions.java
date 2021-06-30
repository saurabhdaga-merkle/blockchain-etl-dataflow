package io.blockchainetl.common;

import org.apache.beam.sdk.options.*;

public interface PubSubToClickhousePipelineOptions extends PipelineOptions, StreamingOptions, SdkHarnessOptions {

    @Description("currency")
    @Validation.Required
    String getCurrency();

    void setCurrency(String value);

    @Description("TigergraphHost")
    String getTigergraphHost();

    void setTigergraphHost(String value);

    @Description("pubSubSubcriptionPrefix")
    String getPubSubSubcriptionPrefix();

    void setPubSubSubcriptionPrefix(String value);

    @Description("Timestamp skew for blocks and transactions, messages older than this will be rejected")
    @Validation.Required
    Long getAllowedTimestampSkewSeconds();

    void setAllowedTimestampSkewSeconds(Long allowedTimestampSkewSeconds);
}
