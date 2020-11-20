package io.blockchainetl.ripple;

import io.blockchainetl.common.utils.JsonUtils;
import io.blockchainetl.ripple.domain.Payments;
import io.blockchainetl.ripple.domain.Reports;

public class temp {
    public static void main(String args[]) {
        String item="{\"type\": \"account_report\", \"hash\": \"F8810F96C2CDB827BBCA0117C4D99FA458A1CA0D3C7BF4483366069DC5625780\", \"executed_time\": \"2020-09-10T15:47:50+00:00\", \"account\": \"r4dgY6Mzob3NVq8CFYdEiPnXKboRScsXRu\", \"amount\": -1.2e-05, \"ledger_index\": 58095851}" +
                "";
        Reports payments = JsonUtils.parseJson(item, Reports.class);
        System.out.print(payments.toString());
    }
}


