package com.nnp.dashboard.scheduler;

import com.nnp.dashboard.model.NnpEnvUsage;
import com.nnp.dashboard.service.NubonsPortalService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "scheduler.usage-data.enabled", havingValue = "true", matchIfMissing = true)
public class UsageDataScheduler extends BaseScheduler {
    
    private final NubonsPortalService nubonsPortalService;
    
    public UsageDataScheduler(NubonsPortalService nubonsPortalService) {
        this.nubonsPortalService = nubonsPortalService;
    }
    
    @Scheduled(cron = "${scheduler.usage-data.cron:0 0 * * * *}")
    public void fetchAndSaveHourlyUsageData() {
        executeWithErrorHandling("USAGE_DATA_COLLECTION", () -> {
            List<NnpEnvUsage> savedEntities = nubonsPortalService.fetchAndSaveLastHourUsageData();
//            logger.info("Successfully processed {} usage records", savedEntities.size());
            return String.format("Saved %d usage records", savedEntities.size());
        });
    }
}