package com.nnp.dashboard.service.environment.strategy;

import com.nnp.dashboard.dto.EnvUsageDto;
import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvUsage;
import org.springframework.stereotype.Component;

/**
 * Strategy for mapping usage data DTOs to entities with environment context.
 */
@Component
public class UsageDataMappingStrategy implements DataMappingStrategy<EnvUsageDto, NnpEnvUsage> {
    
    @Override
    public String extractEnvironmentKey(EnvUsageDto dto) {
        return dto != null ? dto.getEnvName() : null;
    }
    
    @Override
    public NnpEnvUsage convertToEntity(EnvUsageDto dto, EnvironmentV4 environment) {
        // Pass environment for envId extraction, but entity will only store envId as string
        return EnvUsageDto.toEntity(dto, environment);
    }

    @Override
    public Class<EnvUsageDto> getDtoClass(){
        return EnvUsageDto.class;
    }
}