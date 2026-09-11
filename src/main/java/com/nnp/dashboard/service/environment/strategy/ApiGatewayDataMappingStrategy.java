package com.nnp.dashboard.service.environment.strategy;

import com.nnp.dashboard.dto.ApigatwayStatsDto;
import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvApigw;
import org.springframework.stereotype.Component;

/**
 * Strategy for mapping API Gateway data DTOs to entities with environment context.
 */
@Component
public class ApiGatewayDataMappingStrategy implements DataMappingStrategy<ApigatwayStatsDto, NnpEnvApigw> {
    
    @Override
    public String extractEnvironmentKey(ApigatwayStatsDto dto) {
        return dto != null ? dto.getEnvCode() : null;
    }
    
    @Override
    public NnpEnvApigw convertToEntity(ApigatwayStatsDto dto, EnvironmentV4 environment) {
        return ApigatwayStatsDto.toEntity(dto, environment);
    }

    @Override
    public Class<ApigatwayStatsDto> getDtoClass() {
        return ApigatwayStatsDto.class;
    }
}