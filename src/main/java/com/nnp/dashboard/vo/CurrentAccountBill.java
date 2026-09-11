package com.nnp.dashboard.vo;

public record CurrentAccountBill(
        String planName,
        double billAmount,
        String id,
        String billDate,
        String currency
){
}
