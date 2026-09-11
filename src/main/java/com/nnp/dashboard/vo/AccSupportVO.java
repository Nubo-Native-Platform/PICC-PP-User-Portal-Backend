package com.nnp.dashboard.vo;

public record AccSupportVO(
        String id,
        String reportDate,
        String resolveDate,
        Long resolveHr,
        String category,
        String subject,
        String priority,
        String status,
        String categoryType,
        String description,
        String accountName,
        String resolution,
        String redmineIssueId
) {
}
