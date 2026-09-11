package com.nnp.dashboard.scheduler;

import com.nnp.dashboard.dto.PodUsageDto;
import com.nnp.dashboard.service.KafkaMessageService;
import com.nnp.dashboard.service.NubonsPortalService;
import com.nnp.dashboard.utils.LogUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "scheduler.pod-usage-data.enabled", havingValue = "true", matchIfMissing = true)
public class PodUsageDataScheduler extends BaseScheduler {
    private final KafkaMessageService kafkaMessageService;
    private final NubonsPortalService nubonsPortalService;
    private final PodUsageCollectionProperties properties;
    private final String podUsageKafkaTopic;

    public PodUsageDataScheduler(KafkaMessageService kafkaMessageService,
                                 NubonsPortalService nubonsPortalService,
                                 PodUsageCollectionProperties properties,
                                 @Value("${kafka.topic.podusage:nnp-pod-usage}") String podUsageKafkaTopic) {
        this.kafkaMessageService = kafkaMessageService;
        this.nubonsPortalService = nubonsPortalService;
        this.properties = properties;
        this.podUsageKafkaTopic = podUsageKafkaTopic;
    }

    @Scheduled(cron = "${scheduler.pod-usage-data.cron:0 * * * * *}")
    public void fetchAndSendPodUsageData() {
        executeWithErrorHandling("POD_USAGE_DATA_COLLECTION", () -> {
            if (!properties.isEnabled()) {
                List<PodUsageDto> legacy = nubonsPortalService.getPodUsageStats();
                List<PodUsageDto> core = legacy.stream().filter(p -> "nnp-core-components".equalsIgnoreCase(p.getNamespace())).toList();
                kafkaMessageService.sendMessage(podUsageKafkaTopic, core);
                return String.format("Sent %d pod records using legacy collection", core.size());
            }

            List<List<String>> chunks = namespaceChunks();
            if (chunks.isEmpty()) {
                logger.warn("POD_USAGE_DATA_COLLECTION has no configured namespaces; no SigNoz query will be made");
                return "No namespaces configured";
            }

            String windowId = Instant.now().toEpochMilli() + "-" + UUID.randomUUID();
            long intervalMs = Math.max(properties.getDelayBetweenChunksMs(),
                    Math.max(1, properties.getCollectionWindowMs() / chunks.size()));
            int published = 0;
            long nextChunkStart = System.currentTimeMillis();
            for (int i = 0; i < chunks.size(); i++) {
                waitUntil(nextChunkStart);
                List<String> chunk = chunks.get(i);
                List<PodUsageDto> records = fetchWithSingleRetry(chunk, i + 1, chunks.size());
                if (records != null) {
                    String key = "pod-usage:" + windowId + ":" + (i + 1) + "/" + chunks.size();
                    kafkaMessageService.sendMessage(podUsageKafkaTopic, key, records);
                    published++;
                    logger.info("Published pod usage chunk {}/{} for namespaces {} ({} records, windowId={})",
                            LogUtils.sanitizeForLog(i + 1), LogUtils.sanitizeForLog(chunks.size()), LogUtils.sanitizeForLog(chunk), LogUtils.sanitizeForLog(records.size()), LogUtils.sanitizeForLog(windowId));
                }
                nextChunkStart += intervalMs;
            }
            return String.format("Published %d/%d pod usage chunks (windowId=%s)", published, chunks.size(), windowId);
        });
    }

    private List<PodUsageDto> fetchWithSingleRetry(List<String> namespaces, int chunkNumber, int totalChunks) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                return nubonsPortalService.getPodUsageStatsForNamespaces(namespaces);
            } catch (Exception exception) {
                if (attempt == 2) {
                    logger.error("POD_USAGE_DATA_COLLECTION chunk {}/{} for namespaces {} failed after retry; skipping Kafka publish : Error :{}",
                            LogUtils.sanitizeForLog(chunkNumber), LogUtils.sanitizeForLog(totalChunks), LogUtils.sanitizeForLog(namespaces), LogUtils.sanitizeForLog(exception));
                    return null;
                }
                logger.warn("POD_USAGE_DATA_COLLECTION chunk {}/{} for namespaces {} failed; retrying once. Error :{}",
                        LogUtils.sanitizeForLog(chunkNumber), LogUtils.sanitizeForLog(totalChunks), LogUtils.sanitizeForLog(namespaces), LogUtils.sanitizeForLog(exception));
            }
        }
        return null;
    }

    private List<List<String>> namespaceChunks() {
        List<String> namespaces = new ArrayList<>(new LinkedHashSet<>(properties.getNamespaces() == null ? List.of() : properties.getNamespaces()));
        namespaces.removeIf(namespace -> namespace == null || namespace.isBlank());
        int batchSize = Math.max(1, properties.getNamespaceBatchSize());
        List<List<String>> chunks = new ArrayList<>();
        for (int start = 0; start < namespaces.size(); start += batchSize) {
            chunks.add(namespaces.subList(start, Math.min(start + batchSize, namespaces.size())));
        }
        return chunks;
    }

    private void waitUntil(long expectedStartMs) {
        long waitMs = expectedStartMs - System.currentTimeMillis();
        if (waitMs <= 0) return;
        try {
            Thread.sleep(waitMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("POD_USAGE_DATA_COLLECTION interrupted while pacing chunks", exception);
        }
    }
}
