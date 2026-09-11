package com.nnp.dashboard.scheduler;

import com.nnp.dashboard.model.NnpEnvPipeline;
import com.nnp.dashboard.service.NubonsPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.pipeline-data.enabled", havingValue = "true", matchIfMissing = true)
public class PipelineDataScheduler extends BaseScheduler{
    private final NubonsPortalService nubonsPortalService;

    @Scheduled(cron = "${scheduler.pipeline-data.cron:0 0 * * * *}")
    public void fetchAndSaveHourlyPipelineData() {
        executeWithErrorHandling("PIPELINE_DATA_COLLECTION", () -> {
            List<NnpEnvPipeline> savedEntities = nubonsPortalService.fetchAndSaveLastHourPipelineStatsData();
//            logger.info("Successfully processed {} pipeline records", savedEntities.size());
            return String.format("Saved %d pipeline records", savedEntities.size());
        });
    }
}
