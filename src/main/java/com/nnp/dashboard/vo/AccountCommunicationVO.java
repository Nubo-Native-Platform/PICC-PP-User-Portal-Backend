package com.nnp.dashboard.vo;

public record AccountCommunicationVO(
        String date,
        String type,
        String category,
        String action,
        String message,
        String details,
        String link
) {
}
