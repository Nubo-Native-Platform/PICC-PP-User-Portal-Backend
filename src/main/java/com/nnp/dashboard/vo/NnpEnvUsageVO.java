package com.nnp.dashboard.vo;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpEnvUsageVO implements Serializable {

	private static final long serialVersionUID = 1L;

    private Long usageId;

    private EnvironmentVO4 nnpEnv;

    private LocalDate usageDate;

    private Integer hour;

    private Integer avgCpu;

    private Integer avgMem;

    private Integer avgStorage;

    private Integer avgPod;
        
    private Integer maxCpu;

    private Integer maxMem;

    private Integer maxStorage;

    private Integer maxPod;
  
}
