package com.nnp.dashboard.scheduler;

import com.nnp.dashboard.model.NnpEnvApigw;
import com.nnp.dashboard.service.NubonsPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.apigateway-data.enabled", havingValue = "true", matchIfMissing = true)
public class ApigatewayDataScheduler extends BaseScheduler{
    private final NubonsPortalService nubonsPortalService;

    @Scheduled(cron = "${scheduler.apigateway-data.cron:0 0 * * * *}")
    public void fetchAndSaveHourlyApigatewayData() {
        executeWithErrorHandling("APIGATEWAY_DATA_COLLECTION", () -> {
            List<NnpEnvApigw> savedEntities = nubonsPortalService.fetchAndSaveLastHourApigwStatsData();
            return String.format("Saved %d apigateway records", savedEntities.size());
        });
    }
}
