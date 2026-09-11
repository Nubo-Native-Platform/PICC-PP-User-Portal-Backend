package com.nnp.dashboard.scheduler;

import com.nnp.dashboard.model.NnpEnvLog;
import com.nnp.dashboard.service.NubonsPortalService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "scheduler.logs-data.enabled", havingValue = "true", matchIfMissing = true)
public class LogsDataScheduler extends BaseScheduler {
    
    private final NubonsPortalService nubonsPortalService;
    
    public LogsDataScheduler(NubonsPortalService nubonsPortalService) {
        this.nubonsPortalService = nubonsPortalService;
    }
    
    @Scheduled(cron = "${scheduler.logs-data.cron:0 0 * * * *}")
    public void fetchAndSaveHourlyLogsData() {
        executeWithErrorHandling("LOGS_DATA_COLLECTION", () -> {
            List<NnpEnvLog> savedEntities = nubonsPortalService.fetchAndSaveLastHourLogsData();
            return String.format("Saved %d log records", savedEntities.size());
        });
    }
}