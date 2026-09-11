package com.nnp.dashboard.client;

import com.nnp.dashboard.dto.PrometheusResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "prometheus.stub.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class PrometheusClientImpl implements PrometheusClient {
    
    private final HttpClientWrapper httpClient;

    public PrometheusClientImpl(
            @Value("${prometheus.url:http://localhost:9090}") String prometheusUrl) {

        this.httpClient = HttpClientWrapper.create(prometheusUrl);
    }
    
    @Override
    public PrometheusResponseDto query(String promQL) {
        return httpClient
                .get("/api/v1/query")
                .queryParam("query", promQL)
                .execute(PrometheusResponseDto.class);
    }
}