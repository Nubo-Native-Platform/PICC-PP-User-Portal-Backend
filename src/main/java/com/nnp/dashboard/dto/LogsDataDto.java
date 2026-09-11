package com.nnp.dashboard.dto;

import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvLog;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LogsDataDto {
    private String namespace;
    private String appName;
    private Integer totalErrors;
    private Integer totalMessages;
    private Integer hour;
    private LocalDate logDate;
    
    public LogsDataDto() {}
    
    public LogsDataDto(String namespace, String appName, Integer totalErrors, Integer totalMessages, Integer hour) {
        this.namespace = namespace;
        this.appName = appName;
        this.totalErrors = totalErrors;
        this.totalMessages = totalMessages;
        this.hour = hour;
        this.logDate = LocalDate.now();
    }
    
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    
    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }
    
    public Integer getTotalErrors() { return totalErrors; }
    public void setTotalErrors(Integer totalErrors) { this.totalErrors = totalErrors; }
    
    public Integer getTotalMessages() { return totalMessages; }
    public void setTotalMessages(Integer totalMessages) { this.totalMessages = totalMessages; }
    
    public Integer getHour() { return hour; }
    public void setHour(Integer hour) { this.hour = hour; }
    
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
    
    public static NnpEnvLog toEntity(LogsDataDto dto, EnvironmentV4 environment) {
        NnpEnvLog entity = new NnpEnvLog();
        // Store envId as string, do not set EnvironmentV4 entity
        if (environment != null) {
            entity.setEnvId(environment.getEnvId());
        } else if (dto.getNamespace() != null) {
            entity.setEnvId(dto.getNamespace().toUpperCase()); // fallback if env is not found
        } else {
            entity.setEnvId(null);
        }
        entity.setAppName(dto.getAppName() != null ? dto.getAppName() : "ALL_APPS");
        entity.setTotErr(dto.getTotalErrors() != null ? dto.getTotalErrors() : 0);
        entity.setTotMsg(dto.getTotalMessages() != null ? dto.getTotalMessages() : 0);
        entity.setHour(dto.getHour() != null ? dto.getHour() : LocalDateTime.now().getHour());
        entity.setLogDate(dto.getLogDate() != null ? dto.getLogDate() : LocalDate.now());
        return entity;
    }
}