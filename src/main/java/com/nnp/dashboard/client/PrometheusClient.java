package com.nnp.dashboard.client;

import com.nnp.dashboard.dto.PrometheusResponseDto;

public interface
PrometheusClient {
    PrometheusResponseDto query(String promQL);
}