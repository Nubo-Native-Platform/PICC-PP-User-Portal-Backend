package com.nnp.dashboard.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpEnvApigwVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long apigwId;
	private EnvironmentVO4 nnpEnv;
	private Integer hour;
	private Long avgRespTime;
	private Long totTranVol;
	private Integer apiSuccess;
	private Integer apiFail;

}
