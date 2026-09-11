package com.nnp.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LokiResponseDto {
    
    private String status;
    private Data data;
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String resultType;
        private List<Stream> result;
        
        public String getResultType() { return resultType; }
        public void setResultType(String resultType) { this.resultType = resultType; }
        
        public List<Stream> getResult() { return result; }
        public void setResult(List<Stream> result) { this.result = result; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stream {
        private Map<String, String> metric;
        private List<String> value;
        
        public Map<String, String> getMetric() { return metric; }
        public void setMetric(Map<String, String> metric) { this.metric = metric; }
        
        public List<String> getValue() { return value; }
        public void setValue(List<String> value) { this.value = value; }
    }
}