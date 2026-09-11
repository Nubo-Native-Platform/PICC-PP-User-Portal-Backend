package com.nnp.dashboard.service.environment.strategy;

import com.nnp.dashboard.dto.LogsDataDto;
import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvLog;
import org.springframework.stereotype.Component;

/**
 * Strategy for mapping logs data DTOs to entities with environment context.
 */
@Component
public class LogsDataMappingStrategy implements DataMappingStrategy<LogsDataDto, NnpEnvLog> {
    
    @Override
    public String extractEnvironmentKey(LogsDataDto dto) {
        return dto != null ? dto.getNamespace() : null;
    }
    
    @Override
    public NnpEnvLog convertToEntity(LogsDataDto dto, EnvironmentV4 environment) {
        return LogsDataDto.toEntity(dto, environment);
    }
    
    @Override
    public Class<LogsDataDto> getDtoClass() {
        return LogsDataDto.class;
    }
}