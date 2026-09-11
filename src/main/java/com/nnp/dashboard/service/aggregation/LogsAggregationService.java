package com.nnp.dashboard.service.aggregation;

import com.nnp.dashboard.dto.LogsDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LogsAggregationService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogsAggregationService.class);
    private static final String AGGREGATED_APP_NAME = "ALL_APPS";
    
    public List<LogsDataDto> aggregateLogData(List<LogsDataDto> rawLogData) {
        if (rawLogData == null || rawLogData.isEmpty()) {
//            logger.warn("No log data provided for aggregation");
            return List.of();
        }
        
//        logger.info("Aggregating {} log records.",
//                   rawLogData.size());
        
        List<LogsDataDto> aggregatedData = this.aggregate(rawLogData);
        
//        logger.info("Aggregation completed. Reduced from {} to {} records",
//                   rawLogData.size(), aggregatedData.size());
        
        return aggregatedData;
    }

    private List<LogsDataDto> aggregate(List<LogsDataDto> logData) {
        if (logData == null || logData.isEmpty()) {
            return List.of();
        }

//        logger.debug("Starting environment-level aggregation for {} records", logData.size());

        Map<LogsAggregationService.AggregationKey, LogsAggregationService.AggregatedMetrics> aggregatedData = logData.stream()
                .collect(Collectors.groupingBy(
                        dto -> new LogsAggregationService.AggregationKey(dto.getNamespace(), dto.getHour(), dto.getLogDate()),
                        Collectors.reducing(
                                new LogsAggregationService.AggregatedMetrics(),
                                this::mapToMetrics,
                                this::combineMetrics
                        )
                ));

        List<LogsDataDto> result = aggregatedData.entrySet().stream()
                .map(entry -> createAggregatedDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

//        logger.debug("Environment-level aggregation completed. {} environments processed", result.size());
        return result;
    }

    private LogsAggregationService.AggregatedMetrics mapToMetrics(LogsDataDto dto) {
        return new LogsAggregationService.AggregatedMetrics(
                dto.getTotalErrors() != null ? dto.getTotalErrors() : 0,
                dto.getTotalMessages() != null ? dto.getTotalMessages() : 0,
                1
        );
    }

    private LogsAggregationService.AggregatedMetrics combineMetrics(LogsAggregationService.AggregatedMetrics m1, LogsAggregationService.AggregatedMetrics m2) {
        return new LogsAggregationService.AggregatedMetrics(
                m1.totalErrors() + m2.totalErrors(),
                m1.totalMessages() + m2.totalMessages(),
                m1.appCount() + m2.appCount()
        );
    }

    private LogsDataDto createAggregatedDto(LogsAggregationService.AggregationKey key, LogsAggregationService.AggregatedMetrics metrics) {
        LogsDataDto dto = new LogsDataDto();
        dto.setNamespace(key.namespace());
        dto.setAppName(AGGREGATED_APP_NAME);
        dto.setTotalErrors(metrics.totalErrors());
        dto.setTotalMessages(metrics.totalMessages());
        dto.setHour(key.hour());
        dto.setLogDate(key.logDate());
        return dto;
    }

    private record AggregationKey(String namespace, Integer hour, java.time.LocalDate logDate) {}
    private record AggregatedMetrics(int totalErrors, int totalMessages, int appCount) {
        public AggregatedMetrics() {
            this(0, 0, 0);
        }
    }
}