package com.nnp.dashboard.client;

import com.nnp.dashboard.dto.LokiResponseDto;

public interface LokiClient {
    LokiResponseDto query(String logQL);
    LokiResponseDto queryRange(String logQL, String start, String end);
}