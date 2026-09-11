package com.nnp.dashboard.client;

import com.nnp.dashboard.dto.PrometheusResponseDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(
        name = "prometheus.stub.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class PrometheusStub implements PrometheusClient {

    @Override
    public PrometheusResponseDto query(String promQL) {
        return createMockResponse(promQL);
    }

    private PrometheusResponseDto createMockResponse(String promQL) {
        PrometheusResponseDto response = new PrometheusResponseDto();
        response.setStatus("success");

        PrometheusResponseDto.Data data = new PrometheusResponseDto.Data();
        data.setResultType("vector");
        data.setResult(createMockResults(promQL));

        response.setData(data);

        return response;
    }

    private List<PrometheusResponseDto.Result> createMockResults(String promQL) {
        long timestamp = System.currentTimeMillis() / 1000;

        PrometheusResponseDto.Result result1 = createResult(
                "dev-environment",
                timestamp,
                getMockValue(promQL, "dev")
        );

        PrometheusResponseDto.Result result2 = createResult(
                "staging-environment",
                timestamp,
                getMockValue(promQL, "staging")
        );

        PrometheusResponseDto.Result result3 = createResult(
                "prod-environment",
                timestamp,
                getMockValue(promQL, "prod")
        );

        return Arrays.asList(result1, result2, result3);
    }

    private PrometheusResponseDto.Result createResult(
            String namespace,
            long timestamp,
            String value
    ) {
        PrometheusResponseDto.Result result = new PrometheusResponseDto.Result();

        Map<String, String> metric = new HashMap<>();
        metric.put("namespace", namespace);

        result.setMetric(metric);
        result.setValue(Arrays.asList(
                String.valueOf(timestamp),
                value
        ));

        return result;
    }

    private String getMockValue(String promQL, String env) {
        if (promQL.contains("cpu")) {
            return env.equals("prod") ? "0.75"
                    : env.equals("staging") ? "0.45" : "0.25";

        } else if (promQL.contains("memory")) {
            return env.equals("prod") ? "8589934592"
                    : env.equals("staging") ? "4294967296" : "2147483648";

        } else if (promQL.contains("storage") || promQL.contains("volume")) {
            return env.equals("prod") ? "107374182400"
                    : env.equals("staging") ? "53687091200" : "21474836480";

        } else if (promQL.contains("pod")) {
            return env.equals("prod") ? "15"
                    : env.equals("staging") ? "8" : "3";
        }

        return "1.0";
    }
}