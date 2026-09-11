package com.nnp.dashboard.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnp.dashboard.model.SignozQuery;
import com.nnp.dashboard.utils.ContentValues;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Builder
public class SignozQueryBuilder {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Builder.Default
    private Long startTime = Instant.now().minusSeconds(3600).toEpochMilli();
    
    @Builder.Default
    private Long endTime = Instant.now().toEpochMilli();
    
    @Builder.Default
    private String requestType = "scalar";
    
    @Builder.Default
    private String schemaVersion = "v1";
    
    @Singular
    private List<SignozQuery.Query> queries;
    
    private SignozQuery.FormatOptions formatOptions;
    
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    public String buildAsJson() {
        try {
            SignozQuery query = buildQuery();
            return OBJECT_MAPPER.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            log.error("Error serializing Signoz query to JSON", e);
            throw new RuntimeException("Failed to serialize query to JSON", e);
        }
    }

    public String buildAsJsonWithTimeFrame(long start, long end) {
        try {
            SignozQuery query = buildQueryWithTimeFrame(start, end);
            return OBJECT_MAPPER.writeValueAsString(query);
        } catch (JsonProcessingException e) {
            log.error("Error serializing Signoz query to JSON", e);
            throw new RuntimeException("Failed to serialize query to JSON", e);
        }
    }

    public SignozQuery buildQueryWithTimeFrame(long start, long end) {
        return SignozQuery.builder()
                .schemaVersion(schemaVersion)
                .start(start)
                .end(end)
                .requestType(requestType)
                .compositeQuery(SignozQuery.CompositeQuery.builder()
                        .queries(queries)
                        .build())
                .formatOptions(formatOptions)
                .variables(variables)
                .build();
    }

    public SignozQuery buildQuery() {
        return SignozQuery.builder()
            .schemaVersion(schemaVersion)
            .start(startTime)
            .end(endTime)
            .requestType(requestType)
            .compositeQuery(SignozQuery.CompositeQuery.builder()
                .queries(queries)
                .build())
            .formatOptions(formatOptions)
            .variables(variables)
            .build();
    }

    public static SignozQueryBuilder lastHour() {
        Instant now = Instant.now();
        return SignozQueryBuilder.builder()
            .startTime(now.minusSeconds(3600).toEpochMilli())
            .endTime(now.toEpochMilli())
            .build();
    }

    public static SignozQueryBuilder timeRange(Instant start, Instant end) {
        return SignozQueryBuilder.builder()
            .startTime(start.toEpochMilli())
            .endTime(end.toEpochMilli())
            .build();
    }

    @Getter
    public enum SignalType {
        METRICS("metrics"),
        LOGS("logs"),
        TRACES("traces");

        private final String value;

        SignalType(String value) {
            this.value = value;
        }

    }

    public static class QueryBuilderHelper {
        private QueryBuilderHelper() {
            /* This utility class should not be instantiated */
        }

        public static SignozQuery.Query buildQuery(String name, SignalType signal, String legend,
                                                   String filterExpression, List<SignozQuery.Aggregation> aggregations,
                                                   List<SignozQuery.GroupBy> groupBy) {
            return buildQuery(name, signal, legend, filterExpression, aggregations, null, groupBy);
        }
        public static SignozQuery.Query buildQuery(String name, SignalType signal, String legend,
                                                   String filterExpression, List<SignozQuery.Aggregation> aggregations) {
            return buildQuery(name, signal, legend, filterExpression, aggregations, null, null);
        }

        public static SignozQuery.Query buildQuery(String name, SignalType signal, String legend,
                                                   String filterExpression, List<SignozQuery.Aggregation> aggregations,
                                                   Integer stepInterval) {
            return buildQuery(name, signal, legend, filterExpression, aggregations, stepInterval, null);
        }

        public static SignozQuery.Query buildQuery(String name, SignalType signal, String legend,
                                                   String filterExpression, List<SignozQuery.Aggregation> aggregations,
                                                   Integer stepInterval, List<SignozQuery.GroupBy> groupBy) {
            int step = (stepInterval != null) ? stepInterval : 3600;
            List<SignozQuery.GroupBy> groupByList;
            if (groupBy != null) {
                groupByList = groupBy;
            } else {
                groupByList = List.of(SignozQuery.GroupBy.builder()
                        .name("k8s.namespace.name")
                        .fieldDataType(ContentValues.STRING)
                        .fieldContext(signal == SignalType.LOGS ? "resource" : "tag")
                        .build());
            }
            return SignozQuery.Query.builder()
                .type("builder_query")
                .spec(SignozQuery.QuerySpec.builder()
                    .name(name)
                    .signal(signal.getValue())
                    .source("")
                    .stepInterval(step)
                    .disabled(false)
                    .filter(filterExpression != null ? SignozQuery.Filter.builder().expression(filterExpression).build() : null)
                    .groupBy(groupByList)
                    .legend(legend)
                    .having(SignozQuery.Having.builder().expression("").build())
                    .aggregations(aggregations)
                    .build())
                .build();
        }
    }

    public static class CommonQueries {
        private CommonQueries() {
            /* This utility class should not be instantiated */
        }

        
        public static SignozQueryBuilder usageMetrics() {
            return SignozQueryBuilder.builder()
                .formatOptions(SignozQuery.FormatOptions.builder()
                    .formatTableResultForUI(true)
                    .fillGaps(false)
                    .build())
                .query(QueryBuilderHelper.buildQuery("maxCpu", SignalType.METRICS, 
                    "max_cpu", null,
                    List.of(SignozQuery.Aggregation.forMetric("container.cpu.usage", "avg", "max", "avg"))))
                .query(QueryBuilderHelper.buildQuery("avgCpu", SignalType.METRICS, 
                    "avg_cpu", null,
                    List.of(SignozQuery.Aggregation.forMetric("container.cpu.usage", "avg", "avg", "avg"))))
                .query(QueryBuilderHelper.buildQuery("maxMemory", SignalType.METRICS, 
                    "max_memory", null,
                    List.of(SignozQuery.Aggregation.forMetric(ContentValues.K8S_POD_MEMORY_USAGE, "avg", "max", "avg"))))
                .query(QueryBuilderHelper.buildQuery("avgMemory", SignalType.METRICS, 
                    "avg_memory", null,
                    List.of(SignozQuery.Aggregation.forMetric(ContentValues.K8S_POD_MEMORY_USAGE, "avg", "avg", "avg"))))
                .query(QueryBuilderHelper.buildQuery("maxStorage", SignalType.METRICS, 
                    "max_storage", null,
                    List.of(SignozQuery.Aggregation.forMetric(ContentValues.K8S_POD_FILESYSTEM_USAGE, "avg", "max", "avg"))))
                .query(QueryBuilderHelper.buildQuery("avgStorage", SignalType.METRICS, 
                    "avg_storage", null,
                    List.of(SignozQuery.Aggregation.forMetric(ContentValues.K8S_POD_FILESYSTEM_USAGE, "avg", "avg", "avg"))))
                .build();
        }

        public static SignozQueryBuilder logsAnalysis() {
            return SignozQueryBuilder.builder()
                .formatOptions(SignozQuery.FormatOptions.builder()
                    .formatTableResultForUI(true)
                    .fillGaps(false)
                    .build())
                .variables(new HashMap<>())
                .query(QueryBuilderHelper.buildQuery("errorCount", SignalType.LOGS, 
                    "error_count", "severity_text in ['ERROR']",
                    List.of(SignozQuery.Aggregation.forExpression("count()"))))
                .query(QueryBuilderHelper.buildQuery("totalCount", SignalType.LOGS, 
                    "total_count", null,
                    List.of(SignozQuery.Aggregation.forExpression("count()"))))
                .build();
        }

        public static SignozQueryBuilder podUsage(){
            return podUsageForNamespaces(List.of());
        }

        /** Builds a pod-usage query constrained at SigNoz, not after ClickHouse has aggregated it. */
        public static SignozQueryBuilder podUsageForNamespaces(Collection<String> namespaces){
            String namespaceFilter = namespaces == null || namespaces.isEmpty() ? null :
                    "k8s.namespace.name IN [" + namespaces.stream()
                            .map(CommonQueries::quoteFilterValue)
                            .collect(Collectors.joining(", ")) + "]";
            List<SignozQuery.GroupBy> groupBy = List.of(
                SignozQuery.GroupBy.builder()
                    .name("k8s.namespace.name")
                    .fieldDataType(ContentValues.STRING)
                    .fieldContext("tag")
                    .build(),
                SignozQuery.GroupBy.builder()
                    .name("k8s.pod.name")
                    .fieldDataType(ContentValues.STRING)
                    .fieldContext("tag")
                    .build()
            );
            return SignozQueryBuilder.builder()
                .formatOptions(
                    SignozQuery.FormatOptions.builder()
                    .formatTableResultForUI(true)
                    .fillGaps(false)
                    .build()
                )
                .variables(new HashMap<>())
                .query(QueryBuilderHelper.buildQuery("avgCpu", SignalType.METRICS,
                    "avg_cpu", namespaceFilter,
                    List.of(SignozQuery.Aggregation.forMetric("k8s.pod.cpu.usage", "avg", "avg", "avg")),
                    60, groupBy))
                .query(QueryBuilderHelper.buildQuery("avgMemory", SignalType.METRICS,
                    "avg_memory", namespaceFilter,
                    List.of(SignozQuery.Aggregation.forMetric(ContentValues.K8S_POD_MEMORY_USAGE, "avg", "avg", "avg")),
                    60, groupBy))
                .query(QueryBuilderHelper.buildQuery("avgStorage", SignalType.METRICS,
                    "avg_storage", namespaceFilter,
                    List.of(SignozQuery.Aggregation.forMetric(ContentValues.K8S_POD_FILESYSTEM_USAGE, "avg", "avg", "avg")),
                    60, groupBy))
                .build();
        }

        private static String quoteFilterValue(String value) {
            return "'" + value.replace("'", "\\'") + "'";
        }
    }
}
