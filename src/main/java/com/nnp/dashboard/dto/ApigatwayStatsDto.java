package com.nnp.dashboard.dto;

import com.nnp.dashboard.model.EnvironmentV4;
import com.nnp.dashboard.model.NnpEnvApigw;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ApigatwayStatsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String envCode;
    private BigDecimal avgResponseTime;
    private Long requestSize;
    private Long responseSize;
    private Long apiSuccess;
    private Long apiFailed;

    public ApigatwayStatsDto( String envCode, BigDecimal avgResponseTime, Long requestSize, Long responseSize, Long apiSuccess, Long apiFailed) {
        this.envCode = envCode;
        this.avgResponseTime = avgResponseTime;
        this.requestSize = requestSize;
        this.responseSize = responseSize;
        this.apiSuccess = apiSuccess;
        this.apiFailed = apiFailed;
    }

    public static NnpEnvApigw toEntity(ApigatwayStatsDto dto, EnvironmentV4 env) {
        NnpEnvApigw entity = new NnpEnvApigw();
        if (env != null) {
            entity.setEnvId(env.getEnvId());
        } else if (dto.getEnvCode() != null) {
            entity.setEnvId(dto.getEnvCode().toUpperCase()); // fallback if env is not found
        } else {
            entity.setEnvId(null);
        }
        entity.setAvgRespTime(dto.getAvgResponseTime() != null ? dto.getAvgResponseTime().longValue() : 0L);
        entity.setTotTranVol((dto.getRequestSize() != null ? dto.getRequestSize() : 0L) + (dto.getResponseSize() != null ? dto.getResponseSize() : 0L));
        entity.setApiSuccess(dto.getApiSuccess() != null ? dto.getApiSuccess().intValue() : 0);
        entity.setApiFail(dto.getApiFailed() != null ? dto.getApiFailed().intValue() : 0);
        entity.setApiDate(LocalDate.now());
        entity.setHour(LocalTime.now().getHour());
        return entity;
    }
}
