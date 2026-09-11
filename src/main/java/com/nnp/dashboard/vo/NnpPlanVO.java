package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpPlanVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String planId;

	@JsonIgnore
	private NnpCountryVO nnpCountry;

	private String roleId;

	private String planName;

	private String planDesc;

	private String planCategory;

	private String planStatus;

	private String planSpotlgt;

	private Float planBasepr;

	private String maxPod;

	private String maxBandw;

	private String maxPct;

	private String maxAction;

	private String minDuration;

	private Integer itemSeq;

	private String nodeUpr;

	private String cpuUpr;

	private String memUpr;

	private String storageUpr;

	private String planAndtlPagelink;

	private Boolean active;

	private String createdBy;

	private String modifiedBy;

	private OffsetDateTime createdOn;

	private OffsetDateTime modifiedOn;

	private Integer leadTime;

}
