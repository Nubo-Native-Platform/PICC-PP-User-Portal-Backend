package com.nnp.dashboard.vo;

public record FetchCurrentBillRequest(
        String accountId,
        String billingId
) {
}
