package com.nnp.dashboard.client;

import com.nnp.dashboard.dto.LokiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LokiClientImpl implements LokiClient {
    
    private final HttpClientWrapper httpClient;
    
    public LokiClientImpl(@Value("${loki.url:http://localhost:3100}") String lokiUrl) {
        this.httpClient = HttpClientWrapper.create(lokiUrl);
    }
    
    @Override
    public LokiResponseDto query(String logQL) {
        return httpClient
                .get("/loki/api/v1/query")
                .queryParam("query", logQL)
                .execute(LokiResponseDto.class);
    }
    
    @Override
    public LokiResponseDto queryRange(String logQL, String start, String end) {
        return httpClient
                .get("/loki/api/v1/query_range")
                .queryParam("query", logQL)
                .queryParam("start", start)
                .queryParam("end", end)
                .execute(LokiResponseDto.class);
    }
}