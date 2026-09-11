package com.nnp.dashboard.service.environment.strategy;

import com.nnp.dashboard.model.EnvironmentV4;


/**
 * Strategy interface for DTO-to-Entity mapping with environment context.
 * This interface follows the Strategy pattern to provide flexible handling
 * of different DTO-to-Entity conversions with proper environment mapping.
 * 
 * @param <D> DTO type (Data Transfer Object from external sources)
 * @param <E> Entity type (Database Entity with environment context)
 */
public interface DataMappingStrategy<D, E> {

    /**
     * Extracts the environment identifier from a DTO.
     * This identifier will be used to look up the environment in the registry.
     * 
     * @param dto The DTO to extract environment key from
     * @return Environment key/identifier (e.g., namespace, envCode, envId)
     */
    String extractEnvironmentKey(D dto);

    /**
     * Converts a DTO to its corresponding entity, given the environment context.
     * 
     * @param dto The DTO to convert (may have null fields for default entities)
     * @param environment The environment context for this DTO
     * @return The converted entity with proper environment association
     */
    E convertToEntity(D dto, EnvironmentV4 environment);

    /**
     * Returns the DTO class that this strategy handles.
     * Used to create default DTO instances for missing environments.
     * 
     * @return DTO class for instantiation
     */
    Class<D> getDtoClass();

    /**
     * Returns a human-readable name for this strategy type.
     * Used for logging and debugging purposes.
     * 
     * @return Strategy type name
     */
    default String getStrategyName() {
        return this.getClass().getSimpleName();
    }
}