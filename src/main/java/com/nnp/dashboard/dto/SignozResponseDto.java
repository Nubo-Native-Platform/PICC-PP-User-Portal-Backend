package com.nnp.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SignozResponseDto {
    private String status;
    private Data1 data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data1 {
        private String type;
        private Data data;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private List<Result> results;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private String queryName;
        private List<Column> columns;
        private List<List<Object>> data;

    }

    @Getter
    @Setter
    @JsonIgnoreProperties
    public static class Column {
        private String name;
        private String queryName;
        private String columnType;

        public String getKey(){
            return columnType.equalsIgnoreCase("group") ? name : queryName;
        }
    }
}
