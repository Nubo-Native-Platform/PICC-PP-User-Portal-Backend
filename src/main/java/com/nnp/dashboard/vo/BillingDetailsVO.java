package com.nnp.dashboard.vo;


import java.util.List;

public record BillingDetailsVO(
        String subscribedPlan,
        String subscribedPlanPrice,
        CurrentBillingDetails currentBillingDetails,
        List<DailyBillDetails> dailyBillDetails,
        List<BillingData> billingData
) {
    public record CurrentBillingDetails(
            String id,
            Float billAmount,
            String billComment,
            String billContact,
            String billDate,
            Float billOpenBalance,
            String billStatus,
            Float billTaxPct,
            Float adjBillAmount
    ) {}

    public record DailyBillDetails(
            String id,
            Float billAmount,
            Integer accCalls,
            Integer accVol

    ) {}

    public record BillingData(
            String id,
            String month,
            String logDate,   // could also be LocalDate
            int usage,
            Float billAmount,
            Float paidAmount,
            String invoiceUrl
    ) {}
}

