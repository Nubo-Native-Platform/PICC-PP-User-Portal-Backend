package com.nnp.dashboard.features.predictioncomparison;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnp.dashboard.dto.KafkaMessageDto;
import com.nnp.dashboard.dto.PodPredictionDto;
import com.nnp.dashboard.dto.PodUsageDto;
import com.nnp.dashboard.exception.DashboardConfigException;
import com.nnp.dashboard.features.predictioncomparison.dtos.PredictionGraphDto;
import com.nnp.dashboard.service.NubonsPortalService;
import com.nnp.dashboard.service.SignozClientService;
import com.nnp.dashboard.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("prediction")
@RequiredArgsConstructor
@Slf4j
public class PredictionComparison {

    // Time Format : 2025-12-08T00:01:27Z (UTC Timestamp)

    private final NubonsPortalService nubonsPortalService;

    private final SignozClientService signozClientService;

    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.metricprediction:nnp-metric-prediction}")
    private String metricpredictionKafkaTopic;

    public record PodsUsageTemp(
            long startTime,
            Map<String, PodUsageDto> podIdentifierAndPodUsageMap
    ) {
    }


    @GetMapping("graph")
    public String renderGraph(Model model) throws ExecutionException, InterruptedException {
        Instant timeNow = Instant.now();
        long oneDayAgoMilli = Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli();
        PredictionGraphDto predictionComparisonWithRealTimeData = getPredictionComparisonWithRealTimeData(oneDayAgoMilli, timeNow.toEpochMilli());
        model.addAttribute("predictionData", predictionComparisonWithRealTimeData);
        return "PredictionGraph";
    }

    @GetMapping("comparison")
    @ResponseBody
    public PredictionGraphDto getPredictionComparisonWithRealTimeData(long start, long end) throws RuntimeException, InterruptedException, ExecutionException {

        List<KafkaMessageDto> predictionMessages
                = nubonsPortalService.fetchKafkaMessagesForTopicInRange(metricpredictionKafkaTopic, start, end);

        List<PodPredictionDto> predictions = predictionMessages.stream().parallel()
                .map(m -> convert(m.getValue()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        Map<Long, PodsUsageTemp> startTimeAndPodUsageMap = new HashMap<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<PodsUsageTemp>> podUsageTempFutures = predictions.stream()
                    .map(p -> p.getPredictions().getFirst().getTimestamp())
                    .map(t -> executor.submit(() -> getPodUsage(t)))
                    .toList();


            for (Future<PodsUsageTemp> podsUsageTempFuture : podUsageTempFutures) {
                PodsUsageTemp podsUsageTemp = podsUsageTempFuture.get();
                startTimeAndPodUsageMap.put(podsUsageTemp.startTime(), podsUsageTemp);
            }

        }

        Map<Long, List<PredictionGraphDto.PodPredictionCoordinates>> minutePodPredictionCoordinateMap = new HashMap<>();
        for (PodPredictionDto podPredictionDto : predictions) {
            long startTime = podPredictionDto.getPredictions().getFirst().getTimestamp();

            PodsUsageTemp actualPodsUsage = startTimeAndPodUsageMap.get(startTime);
            List<PredictionGraphDto.PodPredictionCoordinates> podsCoordinates = podPredictionDto.getPredictions().stream()
                    .map(ppm -> {
                        PodUsageDto actualPodUsage
                                = actualPodsUsage.podIdentifierAndPodUsageMap.get(getPodIdentifier(ppm.getNamespace(), ppm.getPod()));

                        if (Objects.isNull(actualPodUsage))
                            return null;

                        return new PredictionGraphDto.PodPredictionCoordinates(
                                ppm.getNamespace(),
                                ppm.getPod(),
                                actualPodUsage.getAvgCpu() * 1000,
                                ppm.getCpu(),
                                convertBytesToMB(actualPodUsage.getAvgMemory()),
                                ppm.getMemory(),
                                convertBytesToMB(actualPodUsage.getAvgStorage()),
                                ppm.getStorage()
                        );

                    })
                    .filter(Objects::nonNull)
                    .toList();

            long minutes = Duration.between(Instant.ofEpochMilli(startTime), Instant.now()).toMinutes();
            minutePodPredictionCoordinateMap.put(minutes, podsCoordinates);
        }

        return new PredictionGraphDto(minutePodPredictionCoordinateMap);
    }

    private PodsUsageTemp getPodUsage(long startTime) {
        Long endTime = Instant.ofEpochMilli(startTime).plusSeconds(60).toEpochMilli();
        List<PodUsageDto> podUsages = signozClientService.fetchPodUsageStats(startTime, endTime);
        return new PodsUsageTemp(
                startTime,
                podUsages.stream().collect(Collectors.toMap(u -> getPodIdentifier(u.getNamespace(), u.getPod()), Function.identity()))
        );
    }

    private String getPodIdentifier(String namespace, String podName) {
        return namespace + "||" + podName;
    }

    private double convertBytesToMB(double bytes) {
        return bytes / (1024 * 1024);
    }

    private Optional<PodPredictionDto> convert(Object obj) {
        try {
            return Optional.of(objectMapper.readValue(obj.toString(), PodPredictionDto.class));
        } catch (JsonProcessingException e) {
            log.error("Not Able To Convert Value For Obj : {}, Error: {}", LogUtils.sanitizeForLog(obj), LogUtils.sanitizeForLog(e));
            return Optional.empty();
        }
    }

}
