package com.nnp.dashboard.service;

import com.nnp.dashboard.client.LokiClient;
import com.nnp.dashboard.dto.LogsDataDto;
import com.nnp.dashboard.dto.LokiResponseDto;
import com.nnp.dashboard.utils.ContentValues;
import com.nnp.dashboard.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LokiClientService {
    
    private static final Logger logger = LoggerFactory.getLogger(LokiClientService.class);
    private final LokiClient lokiClient;

    public record LogQuery(String name, String logQL) {}
    public record LogResult(String namespace, String appName, int count) {}
    public record LogData(String queryType, Map<String, Map<String, Integer>> values) {}

    public LokiClientService(LokiClient lokiClient) {
        this.lokiClient = lokiClient;
    }

    public List<LogsDataDto> fetchLogsForAll() {
        try {
            List<LogQuery> queries = List.of(
                new LogQuery("errorLogs", "count_over_time({cluster=\"nnp-cluster-dev-1\", namespace=~\".*\", pod=~\".*\"} |~ \"(?i)error|fatal\" [1h])"),
                new LogQuery("totalLogs", "count_over_time({cluster=\"nnp-cluster-dev-1\", namespace=~\".*\", pod=~\".*\"} |~ \"(?i)\" [1h])")
            );

            List<CompletableFuture<LogData>> futures = queries.stream()
                .map(query -> CompletableFuture.supplyAsync(() ->
                    new LogData(query.name(), queryLogData(query.logQL()))))
                .toList();

            Map<String, Map<String, Map<String, Integer>>> allLogData = futures.stream()
                .map(CompletableFuture::join)
                .collect(HashMap::new,
                    (map, logData) -> map.put(logData.queryType(), logData.values()),
                    HashMap::putAll);

            Set<String> allKeys = allLogData.values().stream()
                .flatMap(logMap -> logMap.keySet().stream())
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

            return allKeys.stream()
                .map(key -> {
                    String[] parts = key.split(":");
                    if (parts.length != 2) return null;

                    String namespace = parts[0];
                    String appName = parts[1];

                    Map<String, Integer> errorData = allLogData.get("errorLogs").getOrDefault(key, Map.of());
                    Map<String, Integer> totalData = allLogData.get("totalLogs").getOrDefault(key, Map.of());

                    return new LogsDataDto(
                        namespace,
                        appName,
                        errorData.getOrDefault(ContentValues.COUNT, 0),
                        totalData.getOrDefault(ContentValues.COUNT, 0),
                        LocalDateTime.now(ZoneId.systemDefault()).getHour()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        } catch (Exception e) {
            logger.error("Error fetching logs data for all environments: {}", LogUtils.sanitizeForLog(e));
            return List.of();
        }
    }

    private Map<String, Map<String, Integer>> queryLogData(String logQL) {
        try {
            LokiResponseDto response = lokiClient.query(logQL);
            return parseLokiResponse(response);
        } catch (Exception e) {
            logger.error("Error executing Loki query: {}, Error:{}", LogUtils.sanitizeForLog(logQL), LogUtils.sanitizeForLog(e));
            return Map.of();
        }
    }

    private Map<String, Map<String, Integer>> parseLokiResponse(LokiResponseDto response) {
        if (response == null || !"success".equals(response.getStatus()) || response.getData() == null) {
            return Map.of();
        }
        
        Map<String, Map<String, Integer>> resultMap = new HashMap<>();
        
        for (LokiResponseDto.Stream stream : response.getData().getResult()) {
            String namespace = stream.getMetric().get("namespace");
            String app = stream.getMetric().get("app");
            
            if (namespace != null && app != null && stream.getValue() != null && stream.getValue().size() > 1) {
                String key = namespace + ":" + app;
                String valueStr = stream.getValue().get(1);
                
                try {
                    int count = Integer.parseInt(valueStr);
                    resultMap.put(key, Map.of(ContentValues.COUNT, count));
                } catch (NumberFormatException e) {
                    logger.error("Failed to parse count value: {}", LogUtils.sanitizeForLog(valueStr));
                    resultMap.put(key, Map.of(ContentValues.COUNT, 0));
                }
            }
        }
        
        return resultMap;
    }
}