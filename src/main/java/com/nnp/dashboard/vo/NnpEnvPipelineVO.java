package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpEnvPipelineVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long plId;

	private EnvironmentVO4 nnpEnv; 

	private LocalDate plDate;

	private Integer hour;

	private Integer avgPlTime;

	private Integer plSuccess;

	private Integer plFail;
	
}
