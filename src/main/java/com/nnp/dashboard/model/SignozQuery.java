package com.nnp.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SignozQuery {
    
    @JsonProperty("schemaVersion")
    private String schemaVersion;
    
    @JsonProperty("start")
    private Long start;
    
    @JsonProperty("end")
    private Long end;
    
    @JsonProperty("requestType")
    private String requestType;
    
    @JsonProperty("compositeQuery")
    private CompositeQuery compositeQuery;
    
    @JsonProperty("formatOptions")
    private FormatOptions formatOptions;
    
    @JsonProperty("variables")
    private Map<String, Object> variables;

    @Data
    @Builder
    public static class CompositeQuery {
        @JsonProperty("queries")
        private List<Query> queries;
    }

    @Data
    @Builder
    public static class Query {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("spec")
        private QuerySpec spec;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class QuerySpec {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("signal")
        private String signal;
        
        @JsonProperty("source")
        private String source;
        
        @JsonProperty("stepInterval")
        private Integer stepInterval;
        
        @JsonProperty("disabled")
        private Boolean disabled;
        
        @JsonProperty("filter")
        private Filter filter;
        
        @JsonProperty("groupBy")
        private List<GroupBy> groupBy;
        
        @JsonProperty("limit")
        private Integer limit;
        
        @JsonProperty("legend")
        private String legend;
        
        @JsonProperty("having")
        private Having having;
        
        @JsonProperty("aggregations")
        private List<Aggregation> aggregations;
    }

    @Data
    @Builder
    public static class Filter {
        @JsonProperty("expression")
        private String expression;
    }

    @Data
    @Builder
    public static class GroupBy {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("fieldDataType")
        private String fieldDataType;
        
        @JsonProperty("fieldContext")
        private String fieldContext;
    }

    @Data
    @Builder
    public static class Having {
        @JsonProperty("expression")
        private String expression;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Aggregation {
        @JsonProperty("metricName")
        private String metricName;
        
        @JsonProperty("timeAggregation")
        private String timeAggregation;
        
        @JsonProperty("spaceAggregation")
        private String spaceAggregation;
        
        @JsonProperty("reduceTo")
        private String reduceTo;
        
        @JsonProperty("expression")
        private String expression;
        
        public static Aggregation forMetric(String metricName, String timeAggregation, 
                                          String spaceAggregation, String reduceTo) {
            return Aggregation.builder()
                .metricName(metricName)
                .timeAggregation(timeAggregation)
                .spaceAggregation(spaceAggregation)
                .reduceTo(reduceTo)
                .build();
        }
        
        public static Aggregation forExpression(String expression) {
            return Aggregation.builder()
                .expression(expression)
                .build();
        }
    }

    @Data
    @Builder
    public static class FormatOptions {
        @JsonProperty("formatTableResultForUI")
        private Boolean formatTableResultForUI;
        
        @JsonProperty("fillGaps")
        private Boolean fillGaps;
    }
}