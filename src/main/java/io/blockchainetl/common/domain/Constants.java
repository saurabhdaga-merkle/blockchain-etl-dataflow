package io.blockchainetl.common.domain;

import org.joda.time.Duration;

public class Constants {
    public static int CH_MAX_INSERT_BLOCK_SIZE = 10000;
    public static int CH_MAX_RETRIES = 15;
    public static Duration CH_INITIAL_BACKOFF_SEC = Duration.standardSeconds(1);
}
