package com.nnp.dashboard.vo.redmine;

import java.util.List;

public record IssuePrioritiesResponse(
        List<IssuePriority> issue_priorities
) {
    public record IssuePriority(
            int id,
            String name,
            boolean is_default,
            boolean active
    ) {}
}
