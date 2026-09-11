package com.nnp.dashboard.dto;

import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvPipeline;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PipelineStatsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String envId;
    private BigDecimal avgPlTime;
    private Long plSuccess;
    private Long plFailed;

    public PipelineStatsDto(String envId, BigDecimal avgPlTime, Long plSuccess, Long plFailed) {
        this.envId = envId;
        this.avgPlTime = avgPlTime;
        this.plSuccess = plSuccess;
        this.plFailed = plFailed;
    }

    public static NnpEnvPipeline toEntity(PipelineStatsDto dto, EnvironmentV4 env) {
        NnpEnvPipeline entity = new NnpEnvPipeline();
        if (env != null) {
            entity.setEnvId(env.getEnvId());
        } else if (dto.getEnvId() != null) {
            entity.setEnvId(dto.getEnvId().toUpperCase()); // fallback if env is not found
        } else {
            entity.setEnvId(null);
        }
        entity.setAvgPlTime(dto.getAvgPlTime() != null ? dto.getAvgPlTime().intValue() : 0);
        entity.setPlSuccess(dto.getPlSuccess() !=null ? dto.getPlSuccess().intValue() : 0);
        entity.setPlFail(dto.getPlFailed() !=null ? dto.getPlFailed().intValue() : 0);
        entity.setPlDate(LocalDate.now(ZoneId.systemDefault()));
        entity.setHour(LocalTime.now(ZoneId.systemDefault()).getHour());
        return entity;
    }
}
