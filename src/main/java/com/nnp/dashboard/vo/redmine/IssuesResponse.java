package com.nnp.dashboard.vo.redmine;

import java.util.List;

public record IssuesResponse(
        List<Issue> issues,
        int total_count,
        int offset,
        int limit
) {
    public record Issue(
            int id,
            Project project,
            Tracker tracker,
            Status status,
            Priority priority,
            Author author,
            Category category,
            Parent parent,
            String subject,
            String description,
            String start_date,
            String due_date,
            int done_ratio,
            boolean is_private,
            Double estimated_hours,
            Double total_estimated_hours,
            double spent_hours,
            double total_spent_hours,
            String created_on,
            String updated_on,
            String closed_on
    ) {
    }

    public record Project(
            int id,
            String name
    ) {
    }

    public record Tracker(
            int id,
            String name
    ) {
    }

    public record Status(
            int id,
            String name,
            boolean is_closed
    ) {
    }

    public record Priority(
            int id,
            String name
    ) {
    }

    public record Author(
            int id,
            String name
    ) {
    }

    public record Category(
            int id,
            String name
    ) {
    }

    public record Parent(
            int id
    ) {
    }
}
