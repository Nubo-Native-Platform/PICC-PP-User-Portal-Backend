package com.nnp.dashboard.features.predictioncomparison.dtos;

import java.util.List;
import java.util.Map;

public record PredictionGraphDto(
        Map<Long ,List<PodPredictionCoordinates>> minutePodPredictionCoordinatesMap
) {
    public record PodPredictionCoordinates(
             String namespace,

             String podName,

             Double cpuActual,
             Double cpuPrediction,

             Double memoryActual,
             Double memoryPrediction,

             Double storageActual,
             Double storagePrediction
    ){}
}
