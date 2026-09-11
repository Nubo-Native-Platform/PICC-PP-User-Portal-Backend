package com.nnp.dashboard.vo.redmine;

import java.util.List;

public record IssueCategoriesResponse(
        List<IssueCategory> issue_categories,
        int total_count
) {
    public record IssueCategory(
            int id,
            Project project,
            String name,
            AssignedTo assigned_to
    ) {}

    public record Project(
            int id,
            String name
    ) {}

    public record AssignedTo(
            int id,
            String name
    ) {}
}