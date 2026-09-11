package com.nnp.dashboard.dto;

import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvUsage;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class EnvUsageDto  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String envName;
    private Double avgCpu;
    private Double maxCpu;
    private Double avgMem;
    private Double maxMem;
    private Double avgStorage;
    private Double maxStorage;
    private Double avgPod;
    private Double maxPod;

    public EnvUsageDto(String envName, Double avgCpu, Double maxCpu, Double avgMem, Double maxMem,
                      Double avgStorage, Double maxStorage, Double avgPod,
                      Double maxPod) {
        this.envName = envName;
        this.avgCpu = avgCpu;
        this.maxCpu = maxCpu;
        this.avgMem = avgMem;
        this.maxMem = maxMem;
        this.avgPod = avgPod;
        this.maxPod = maxPod;
        this.avgStorage = avgStorage;
        this.maxStorage = maxStorage;
    }

    public static NnpEnvUsage toEntity(EnvUsageDto dto, EnvironmentV4 env) {
        NnpEnvUsage nnpEnvUsage = new NnpEnvUsage();
        // Store envId as string, do not set EnvironmentV4 entity
        if (env != null) {
            nnpEnvUsage.setEnvId(env.getEnvId());
        } else if (dto.getEnvName() != null) {
            nnpEnvUsage.setEnvId(dto.getEnvName().toUpperCase()); // fallback if env is not found
        } else {
            nnpEnvUsage.setEnvId(null);
        }
        nnpEnvUsage.setAvgCpu(dto.getAvgCpu() != null ? dto.getAvgCpu().intValue() : 0);
        nnpEnvUsage.setMaxCpu(dto.getMaxCpu() != null ? dto.getMaxCpu().intValue() : 0);
        nnpEnvUsage.setAvgMem(dto.getAvgMem() != null ? dto.getAvgMem().intValue() : 0);
        nnpEnvUsage.setMaxMem(dto.getMaxMem() != null ? dto.getMaxMem().intValue() : 0);
        nnpEnvUsage.setAvgStorage(dto.getAvgStorage() != null ? dto.getAvgStorage().intValue() : 0);
        nnpEnvUsage.setMaxStorage(dto.getMaxStorage() != null ? dto.getMaxStorage().intValue() : 0);
        nnpEnvUsage.setAvgPod(dto.getAvgPod() != null ? dto.getAvgPod().intValue() : 0);
        nnpEnvUsage.setMaxPod(dto.getMaxPod() != null ? dto.getMaxPod().intValue() : 0);
        nnpEnvUsage.setUsageDate(LocalDate.now());
        nnpEnvUsage.setHour(LocalTime.now().getHour());
        return nnpEnvUsage;
    }

}
