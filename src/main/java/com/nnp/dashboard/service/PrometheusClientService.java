package com.nnp.dashboard.service;

import com.nnp.dashboard.client.PrometheusClient;
import com.nnp.dashboard.dto.EnvUsageDto;
import com.nnp.dashboard.dto.PrometheusResponseDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class PrometheusClientService {
    
    public record MetricQuery(String name, String promQL) {}
    
    public record MetricResult(String namespace, double value) {}
    
    public record MetricData(String metricType, Map<String, Double> values) {}
    private final PrometheusClient prometheusClient;
    
    public PrometheusClientService(PrometheusClient prometheusClient) {
        this.prometheusClient = prometheusClient;
    }

    public List<EnvUsageDto> fetchUsageForAll() {
        List<MetricQuery> queries = List.of(
            new MetricQuery("avgCpu", "avg(rate(container_cpu_usage_seconds_total[1h])) by (namespace)"),
            new MetricQuery("maxCpu", "max(rate(container_cpu_usage_seconds_total[1h])) by (namespace)"),
            new MetricQuery("avgMem", "avg(container_memory_usage_bytes) by (namespace)"),
            new MetricQuery("maxMem", "max(container_memory_usage_bytes) by (namespace)"),
            new MetricQuery("avgStorage", "avg_over_time( (sum(kubelet_volume_stats_used_bytes) by (namespace))[1h:])"),
            new MetricQuery("maxStorage", "sum(kubelet_volume_stats_capacity_bytes) by (namespace)"),
            new MetricQuery("avgPod", "avg_over_time((count(kube_pod_info) by (namespace))[1h:])"),
            new MetricQuery("maxPod", "count(kube_pod_info) by (namespace)")
        );

        List<CompletableFuture<MetricData>> futures = queries.stream()
            .map(query -> CompletableFuture.supplyAsync(() -> 
                new MetricData(query.name(), queryMap(query.promQL()))))
            .toList();

        Map<String, Map<String, Double>> allMetrics = futures.stream()
            .map(CompletableFuture::join)
            .collect(HashMap::new, 
                (map, metricData) -> map.put(metricData.metricType(), metricData.values()),
                HashMap::putAll);

        Set<String> allNamespaces = allMetrics.values().stream()
            .flatMap(metricMap -> metricMap.keySet().stream())
            .collect(HashSet::new, HashSet::add, HashSet::addAll);

        return allNamespaces.stream()
            .map(namespace -> new EnvUsageDto(
                namespace,
                allMetrics.get("avgCpu").getOrDefault(namespace, 0.0),
                allMetrics.get("maxCpu").getOrDefault(namespace, 0.0),
                allMetrics.get("avgMem").getOrDefault(namespace, 0.0),
                allMetrics.get("maxMem").getOrDefault(namespace, 0.0),
                allMetrics.get("avgStorage").getOrDefault(namespace, 0.0),
                allMetrics.get("maxStorage").getOrDefault(namespace, 0.0),
                allMetrics.get("avgPod").getOrDefault(namespace, 0.0),
                allMetrics.get("maxPod").getOrDefault(namespace, 0.0)
            ))
            .toList();
    }

    private Map<String, Double> queryMap(String promQl) {
        try {
            PrometheusResponseDto response = prometheusClient.query(promQl);
            return parsePrometheusResponse(response);
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    private Map<String, Double> parsePrometheusResponse(PrometheusResponseDto response) {
        if (response == null || !"success".equals(response.getStatus()) || response.getData() == null) {
            return Map.of();
        }
        
        return response.getData().getResult().stream()
            .map(result -> new MetricResult(
                result.getMetric().get("namespace"),
                Double.parseDouble(result.getValue().get(1))
            ))
            .filter(metricResult -> metricResult.namespace() != null)
            .collect(HashMap::new,
                (map, metricResult) -> map.put(metricResult.namespace(), metricResult.value()),
                HashMap::putAll);
    }

}
