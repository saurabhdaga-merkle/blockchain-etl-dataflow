public class temp {
    public static void main(String args[]) {
        item='{"type": "payment", "hash": "A4A5B464F3481FC0AEB994DB2735D07C22930CBE9838AB143ED020DD66023243", "executed_time": "2020-09-05T15:48:50+00:00", "account": "rwpTh9DDa52XkM9nTKp2QrJuCGV5d1mQVP", "destination": "rCoinaUERUrXb1aA7dJu8qRcmvPNiKS3d", "delivered_amount": "23677657", "ledger_index": "57986715", "fee": "40", "source_tag": "-1", "destination_tag": "1111699672"}'
        Payments payments = JsonUtils.parseJson(item, Payments.class);
        System.out.prit(payments.toString());
    }
}
