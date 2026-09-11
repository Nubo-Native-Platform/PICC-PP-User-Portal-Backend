package com.nnp.dashboard.features.predictioncomparison;

import com.nnp.dashboard.dto.PodPredictionDto;
import com.nnp.dashboard.dto.PodPredictionMessageDto;
import com.nnp.dashboard.exception.DashboardConfigException;
import com.nnp.dashboard.features.predictioncomparison.dtos.AllK8sPodsResponse;
import com.nnp.dashboard.service.NubonsPortalService;
import com.nnp.dashboard.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@ConditionalOnBooleanProperty(value = "prediction.test.schedular.active")
@Slf4j
public class TestPredictionGeneratorScheduler {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${k8sintg.service.url:http://localhost:8082}")
    private String k8sIntgServiceBaseUrl;

    private final NubonsPortalService nubonsPortalService;

    @Autowired
    public TestPredictionGeneratorScheduler(
            NubonsPortalService nubonsPortalService
    ) {
        this.nubonsPortalService = nubonsPortalService;
    }

    public void populatePredictionKafkaTopic() {

        String insertionId = Uuid.randomUuid().toString();

        log.info(
                "Scheduler Start With Insertion Id : {} At UTC Epoch Seconds Time : {}",
                LogUtils.sanitizeForLog(insertionId),
                LogUtils.sanitizeForLog(Instant.now().getEpochSecond())
        );

        AllK8sPodsResponse allK8sPods = getAllK8sPods();

        log.info(
                "Fetched Data For All Kubernetes Pods, No Of Pods : {}",
                LogUtils.sanitizeForLog(allK8sPods.pods().size())
        );

        PodPredictionDto podPredictionDto = new PodPredictionDto(
                insertionId,
                allK8sPods.pods()
                        .stream()
                        .map(this::generatePodPredictionMessageDto)
                        .toList()
        );

        nubonsPortalService.sendPredctionToTopic(podPredictionDto);

        log.info("Successfully Appended Data To Kafka Logs");
    }

    private AllK8sPodsResponse getAllK8sPods() {

        RestClient restClient = RestClient.create();

        ResponseEntity<AllK8sPodsResponse> allPodsResponse =
                restClient.get()
                        .uri(
                                k8sIntgServiceBaseUrl
                                        + "/k8s-intg/admin/pods"
                        )
                        .retrieve()
                        .toEntity(AllK8sPodsResponse.class);

        if (!allPodsResponse.getStatusCode().is2xxSuccessful()) {
            throw new DashboardConfigException(
                    "500",
                    "Error Getting All Pods From K8s Intg Service With Response Code : "
                            + allPodsResponse.getStatusCode().value()
            );
        }

        return allPodsResponse.getBody();
    }

    private PodPredictionMessageDto generatePodPredictionMessageDto(
            AllK8sPodsResponse.K8sPod pod
    ) {

        double minCpu = 0;
        double maxCpu = 4.431;

        long minMemory = 2547712L;
        long maxMemory = 10405180075L;

        long minStorage = 8192L;
        long maxStorage = 29684821197L;

        return new PodPredictionMessageDto(
                Instant.now()
                        .plus(2, ChronoUnit.MINUTES)
                        .toEpochMilli(),

                pod.namespace(),
                pod.name(),

                generateNumberBetweenMinAndMax(minCpu, maxCpu),
                getRandomProbability(),

                (double) generateLongBetweenMinAndMax(
                        minMemory,
                        maxMemory
                ),
                getRandomProbability(),

                (double) generateLongBetweenMinAndMax(
                        minStorage,
                        maxStorage
                ),
                getRandomProbability()
        );
    }

    private double generateNumberBetweenMinAndMax(
            double min,
            double max
    ) {
        return min
                + SECURE_RANDOM.nextDouble()
                * (max - min);
    }

    private long generateLongBetweenMinAndMax(
            long min,
            long max
    ) {
        return SECURE_RANDOM.nextLong(min, max + 1);
    }

    private double getRandomProbability() {
        return SECURE_RANDOM.nextDouble();
    }
}