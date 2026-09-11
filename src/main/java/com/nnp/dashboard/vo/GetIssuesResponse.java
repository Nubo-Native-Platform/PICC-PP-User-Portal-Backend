package com.nnp.dashboard.vo;

import java.util.List;

public record GetIssuesResponse(
        int numberOfPages,
        int pageSize,
        List<AccSupportVO> issues
) {
}
