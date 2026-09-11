package com.nnp.dashboard.dto;

import com.nnp.dashboard.vo.NnpAccountVO;

import java.util.List;

public record AllAccountsResponse (
        List<NnpAccountVO> content,
        int totalPages,
        long totalElements,
        int pageSize,
        int pageNumber
){
}
