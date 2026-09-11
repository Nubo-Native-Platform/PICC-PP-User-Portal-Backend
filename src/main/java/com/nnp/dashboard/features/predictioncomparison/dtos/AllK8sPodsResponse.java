package com.nnp.dashboard.features.predictioncomparison.dtos;

import java.util.List;

public record AllK8sPodsResponse(
        List<K8sPod> pods
) {

    public record K8sPod(
            String name,
            String namespace
    ) {
    }
}
