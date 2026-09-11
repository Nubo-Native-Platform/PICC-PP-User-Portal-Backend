package com.nnp.dashboard.service.environment;

import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.repo.EnvironmentRepoV3;
import com.nnp.dashboard.service.environment.strategy.DataMappingStrategy;
import com.nnp.dashboard.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that provides flexible DTO-to-Entity mapping with environment context.
 * Uses the Strategy pattern to handle different entity types in a consistent manner.
 * 
 * Provides two mapping modes:
 * 1. Complete Mapping: Transform DTOs to entities AND ensure all environments are included with defaults
 * 2. Present-Only Mapping: Transform only DTOs that have matching environments (original behavior)
 * 
 * Core responsibilities:
 * - Convert DTOs to entities using environment registry for context
 * - Support flexible mapping strategies via Strategy pattern
 * - Optionally fill gaps with default entities for missing environments
 */
@Service
public class EnvironmentDataMappingService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentDataMappingService.class);
    private final EnvironmentRepoV3 envRepositoryV3;
    private final ModelMapper mapper;

    public EnvironmentDataMappingService(ModelMapper mapper, EnvironmentRepoV3 envRepositoryV3) {
        this.mapper = mapper;
        this.envRepositoryV3 = envRepositoryV3;
    }

    /**
     * Maps DTOs to entities and ensures ALL environments are represented in the result.
     * Missing environments will get default entities with zero/default values.
     *
     * @param dtoList List of DTOs containing actual data from external sources
     * @param environmentRegistry Map of all available environments (envCode -> Environment)
     * @param mappingStrategy Strategy that defines how to handle the specific DTO/Entity conversion
     * @return Complete list of entities covering ALL environments (data + defaults)
     */
    public <D, E> List<E> mapWithAllEnvironments(
            List<D> dtoList,
            Map<String, EnvironmentV4> environmentRegistry,
            DataMappingStrategy<D, E> mappingStrategy) {
        return mapData(dtoList, environmentRegistry, mappingStrategy, true, true);
    }
    
    /**
     * Maps DTOs to entities ONLY for environments that have actual data.
     * Environments without data are ignored and not included in the result.
     *
     * @param dtoList List of DTOs containing actual data from external sources
     * @param environmentRegistry Map of all available environments (envCode -> Environment)
     * @param mappingStrategy Strategy that defines how to handle the specific DTO/Entity conversion
     * @return List of entities only for environments with actual data
     */
    public <D, E> List<E> mapPresentEnvironmentsOnly(
            List<D> dtoList,
            Map<String, EnvironmentV4> environmentRegistry,
            DataMappingStrategy<D, E> mappingStrategy) {
        return mapData(dtoList, environmentRegistry, mappingStrategy, false, true);
    }
    
    private <D, E> List<E> mapData(
            List<D> dtoList,
            Map<String, EnvironmentV4> environmentRegistry,
            DataMappingStrategy<D, E> mappingStrategy,
            boolean includeAllEnvironments,
            boolean includeIgnorable) {

        if (environmentRegistry == null || environmentRegistry.isEmpty()) {
            return List.of();
        }
        List<E> entitiesWithData = convertDtosToEntities(dtoList, environmentRegistry, mappingStrategy, includeIgnorable);

        if (!includeAllEnvironments) {
            return entitiesWithData;
        }
        Set<String> processedEnvironments = extractProcessedEnvironments(dtoList, environmentRegistry, mappingStrategy);
        List<E> defaultEntities = createDefaultEntitiesForMissingEnvironments(
                environmentRegistry, processedEnvironments, mappingStrategy);
        return combineEntities(entitiesWithData, defaultEntities);
    }
    
    private <D, E> List<E> convertDtosToEntities(
            List<D> dtoList,
            Map<String, EnvironmentV4> environmentRegistry,
            DataMappingStrategy<D, E> strategy,
            boolean includeIgnorable) {
        List<E> entitiesWithData = new ArrayList<>();
        if (dtoList == null || dtoList.isEmpty()) {
            return entitiesWithData;
        }
        for (D dto : dtoList) {
            String environmentKey = strategy.extractEnvironmentKey(dto);
            if (environmentKey == null) {
              continue;
            }
            EnvironmentV4 environment = environmentRegistry.get(environmentKey.toLowerCase());
            if (environment == null) {
                if (includeIgnorable) {
                    EnvironmentV4 blankEnv = new EnvironmentV4();
                    blankEnv.setEnvId(environmentKey.toUpperCase());
                    blankEnv.setEnvCode(environmentKey.toUpperCase());
                    E entity = strategy.convertToEntity(dto, blankEnv);
                    if (entity != null) {
                        entitiesWithData.add(entity);
                    }
                }
            } else {
                E entity = strategy.convertToEntity(dto, environment);
                if (entity != null) {
                    entitiesWithData.add(entity);
                }
            }
        }
        return entitiesWithData;
    }

    private <E, D> Set<String> extractProcessedEnvironments(
            List<D> dtoList,
            Map<String, EnvironmentV4> environmentRegistry,
            DataMappingStrategy<D, E> strategy) {
        return dtoList.stream()
                .map(d -> environmentRegistry.get(strategy.extractEnvironmentKey(d)))
                .filter(Objects::nonNull)
                .map(e -> e.getEnvCode().toLowerCase())
                .collect(Collectors.toSet());
    }
    
    private <D, E> List<E> createDefaultEntitiesForMissingEnvironments(
            Map<String, EnvironmentV4> environmentRegistry,
            Set<String> processedEnvironments,
            DataMappingStrategy<D, E> strategy) {
        
        return environmentRegistry.values().stream()
                .filter(env -> !processedEnvironments.contains(env.getEnvCode().toLowerCase()))
                .map(env -> {
                    try {
                        Class<D> dtoClass = strategy.getDtoClass();
                        D defaultDto = dtoClass.getDeclaredConstructor().newInstance();
                        return strategy.convertToEntity(defaultDto, env);
                    } catch (Exception e) {
                        logger.error("Error creating default DTO instance for strategy: {}, Error:{}", LogUtils.sanitizeForLog( strategy.getStrategyName()),LogUtils.sanitizeForLog( e));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    private <E> List<E> combineEntities(List<E> entitiesWithData, List<E> defaultEntities) {
        List<E> combined = new ArrayList<>();
        combined.addAll(entitiesWithData);
        combined.addAll(defaultEntities);
        return combined;
    }
    
    /**
     * Builds a map of all available environments from the database.
     * Key: environment code (lowercase), Value: EnvironmentV4 entity
     * 
     * @return Map of environment code to EnvironmentV4 entities
     */
    public Map<String, EnvironmentV4> buildEnvironmentMap() {
        return envRepositoryV3.findAll().stream()
            .map(e -> mapper.map(e, EnvironmentV4.class))
            .collect(Collectors.toMap(
                e -> e.getEnvCode().toLowerCase(),
                e -> e,
                (existing, replacement) -> {
                    logger.error("Duplicate environment code found: {}", LogUtils.sanitizeForLog(existing.getEnvCode()));
                    return existing;
                }
            ));
    }
}