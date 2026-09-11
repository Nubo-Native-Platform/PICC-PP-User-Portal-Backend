package com.nnp.dashboard.client;

import com.nnp.dashboard.dto.SignozResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SignozClientImpl implements SignozClient{

    private final HttpClientWrapper httpClient;
    private final String signozUrl;
    private final String signozApiKey;


    public SignozClientImpl(@Value("${signoz.url:http://localhost:3301}") String signozUrl, @Value("${signoz.apiKey:}") String signozApiKey) {
        this.signozUrl = signozUrl;
        this.signozApiKey = signozApiKey;
        this.httpClient = HttpClientWrapper.create(signozUrl);
    }

    @Override
    public SignozResponseDto queryRange(String query) {
        return this.httpClient.post(signozUrl+"/api/v5/query_range").body(query).header("SIGNOZ-API-KEY", signozApiKey).execute(SignozResponseDto.class);
    }
}
