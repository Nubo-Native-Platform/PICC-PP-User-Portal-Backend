package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpPlanCompVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String planCompId;

	private String planCompName;

	private String planCompDesp;

	private EnvironmentVO4 env;

	private NnpPlanVO nnpPlan;

	private Boolean active = true;

	private String createdBy;

	private String modifiedBy;

	private ZonedDateTime createdOn;

	private ZonedDateTime modifiedOn;

}
