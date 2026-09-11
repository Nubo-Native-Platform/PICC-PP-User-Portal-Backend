package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpAccountPlanCompVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accPlanCompId;

	private NnpPlanCompVO nnpPlanComp;

	private NnpAccountPlanVO nnpAccountPlan;

	private Boolean active = true;

	private String createdBy;

	private String modifiedBy;

	private ZonedDateTime createdOn;

	private ZonedDateTime modifiedOn;

}
