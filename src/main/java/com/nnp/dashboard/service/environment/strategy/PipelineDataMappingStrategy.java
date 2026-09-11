package com.nnp.dashboard.service.environment.strategy;

import com.nnp.dashboard.dto.PipelineStatsDto;
import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvPipeline;
import org.springframework.stereotype.Component;

/**
 * Strategy for mapping pipeline data DTOs to entities with environment context.
 */
@Component
public class PipelineDataMappingStrategy implements DataMappingStrategy<PipelineStatsDto, NnpEnvPipeline> {
    
    @Override
    public String extractEnvironmentKey(PipelineStatsDto dto) {
        return dto != null ? dto.getEnvId() : null;
    }
    
    @Override
    public NnpEnvPipeline convertToEntity(PipelineStatsDto dto, EnvironmentV4 environment) {
        return PipelineStatsDto.toEntity(dto, environment);
    }

    @Override
    public Class<PipelineStatsDto> getDtoClass(){
        return PipelineStatsDto.class;
    }
}