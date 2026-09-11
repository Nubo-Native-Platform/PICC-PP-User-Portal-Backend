package com.nnp.dashboard.vo.redmine;

import java.util.List;

public record IssueStatusesResponse(
        List<IssueStatus> issue_statuses
) {
    public record IssueStatus(
            int id,
            String name,
            boolean is_closed,
            String description
    ) {}
}