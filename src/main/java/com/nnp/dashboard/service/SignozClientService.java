package com.nnp.dashboard.service;

import com.nnp.dashboard.builder.SignozQueryBuilder;
import com.nnp.dashboard.client.SignozClient;
import com.nnp.dashboard.dto.EnvUsageDto;
import com.nnp.dashboard.dto.LogsDataDto;
import com.nnp.dashboard.dto.PodUsageDto;
import com.nnp.dashboard.dto.SignozResponseDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for querying telemetry, resource usage metrics, and log analysis from SigNoz APM platform.
 * Formats SigNoz response data into DTO representations for environment resource usage, pod metrics, and log statistics.
 */
@Service
public class SignozClientService {
    private final SignozClient signozClient;

    public SignozClientService(SignozClient signozClient) {
        this.signozClient = signozClient;
    }

    public List<EnvUsageDto> fetchUsageForAll() {
        String query = SignozQueryBuilder.CommonQueries.usageMetrics().buildAsJson();
        SignozResponseDto resp = this.signozClient.queryRange(query);

        SignozResponseDto.Result res = resp.getData().getData().getResults().get(0);
        List<String> columns = res.getColumns().stream().map(c-> c.getKey()).toList();
        return res.getData().stream().map(d -> {
            String envName = columns.contains("k8s.namespace.name") && d.size() > columns.indexOf("k8s.namespace.name") ? (String)((List<?>)d).get(columns.indexOf("k8s.namespace.name")) : "unknown";
            Double avgCpu = columns.contains("avgCpu") && d.size() > columns.indexOf("avgCpu") ? getValue(((List<?>)d).get(columns.indexOf("avgCpu"))) : 0.0;
            Double maxCpu = columns.contains("maxCpu") && d.size() > columns.indexOf("maxCpu") ? getValue(((List<?>)d).get(columns.indexOf("maxCpu"))) : 0.0;
            Double avgMemory = columns.contains("avgMemory") && d.size() > columns.indexOf("avgMemory") ? getValue(((List<?>)d).get(columns.indexOf("avgMemory"))) : 0.0;
            Double maxMemory = columns.contains("maxMemory") &&d.size() > columns.indexOf("maxMemory") ? getValue(((List<?>)d).get(columns.indexOf("maxMemory"))) : 0.0;
            Double avgStorage = columns.contains("avgStorage") && d.size() > columns.indexOf("avgStorage") ? getValue(((List<?>)d).get(columns.indexOf("avgStorage"))) : 0.0;
            Double maxStorage = columns.contains("maxStorage") && d.size() > columns.indexOf("maxStorage") ? getValue(((List<?>)d).get(columns.indexOf("maxStorage"))) : 0.0;
            return new EnvUsageDto(
                envName,
                avgCpu * 1000,
                maxCpu * 1000,
                avgMemory > 0 ? avgMemory/1000 : 0.0,
                maxMemory > 0 ? maxMemory/1000 : 0.0,
                avgStorage > 0 ? avgStorage/1000000 : 0.0,
                maxStorage > 0 ? maxStorage/1000000 : 0.0,
                0.0, // avgPod not implemented
                0.0  // maxPod not implemented
            );
        }).toList();
    }

    public List<LogsDataDto> fetchLogsForAll() {
        String query = SignozQueryBuilder.CommonQueries.logsAnalysis().buildAsJson();
        SignozResponseDto resp = this.signozClient.queryRange(query);

        SignozResponseDto.Result res = resp.getData().getData().getResults().get(0);
        List<String> columns = res.getColumns().stream().map(c-> c.getKey()).toList();
        return res.getData().stream().map(d -> new LogsDataDto(
                d.size() > columns.indexOf("k8s.namespace.name") ? (String)((List<?>)d).get(columns.indexOf("k8s.namespace.name")) : "unknown",
                "ALL_APPS",
                d.size() > columns.indexOf("errorCount") ? getValue(((List<?>)d).get(columns.indexOf("errorCount"))).intValue() : 0,
                d.size() > columns.indexOf("totalCount") ? getValue(((List<?>)d).get(columns.indexOf("totalCount"))).intValue() : 0,
                LocalDateTime.now().getHour()
        )).toList();
    }

    public List<PodUsageDto> fetchPodUsageStats(){
        return fetchPodUsageStats(0, 0);
    }

    public List<PodUsageDto> fetchPodUsageStats(long start, long end){
        return fetchPodUsageStats(List.of(), start, end);
    }

    public List<PodUsageDto> fetchPodUsageStatsForNamespaces(List<String> namespaces) {
        return fetchPodUsageStats(namespaces, 0, 0);
    }

    private List<PodUsageDto> fetchPodUsageStats(List<String> namespaces, long start, long end){
        String query;
        // 0 ,0 Start And End Times Means Default Values In SignozQueryBuilder Will Be Used
        if (start == 0 && end == 0){
            query = SignozQueryBuilder.CommonQueries
                    .podUsageForNamespaces(namespaces)
                    .buildAsJson();
        }else {
            query = SignozQueryBuilder.CommonQueries
                    .podUsageForNamespaces(namespaces)
                    .buildAsJsonWithTimeFrame(start, end);
        }

        SignozResponseDto resp = this.signozClient.queryRange(query);
        SignozResponseDto.Result res = resp.getData().getData().getResults().get(0);
        List<String> columns = res.getColumns().stream().map(c-> c.getKey()).toList();
        return res.getData().stream().map( p -> {
            String namespaceName = columns.contains("k8s.namespace.name") && p.size() > columns.indexOf("k8s.namespace.name") ? (String)((List<?>)p).get(columns.indexOf("k8s.namespace.name")) : "unknown";
            String podName = columns.contains("k8s.pod.name") && p.size() > columns.indexOf("k8s.pod.name") ? (String)((List<?>)p).get(columns.indexOf("k8s.pod.name")) : "unknown";
            Double avgCpu = columns.contains("avgCpu") && p.size() > columns.indexOf("avgCpu") ? getValue(((List<?>)p).get(columns.indexOf("avgCpu"))) : 0.0;
            Double avgMemory = columns.contains("avgMemory") && p.size() > columns.indexOf("avgMemory") ? getValue(((List<?>)p).get(columns.indexOf("avgMemory"))) : 0.0;
            Double avgStorage = columns.contains("avgStorage") && p.size() > columns.indexOf("avgStorage") ? getValue(((List<?>)p).get(columns.indexOf("avgStorage"))) : 0.0;

            return new PodUsageDto(namespaceName, podName, avgCpu, avgMemory, avgStorage);
        }).toList();
    }

    private Double getValue(Object val) {
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return 0.0;
    }

}
