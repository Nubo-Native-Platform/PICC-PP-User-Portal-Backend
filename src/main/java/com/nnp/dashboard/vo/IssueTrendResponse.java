package com.nnp.dashboard.vo;

import java.util.List;

public record IssueTrendResponse(
        String issueCategory,
        int totalIssueCount,
        List<MonthlyIssueData> monthlyData
) {
    public record MonthlyIssueData(
            String month,
            int year,
            int resolvedIssue,
            int reportedIssue,
            Double averageResolveTime
    ) {}
}
