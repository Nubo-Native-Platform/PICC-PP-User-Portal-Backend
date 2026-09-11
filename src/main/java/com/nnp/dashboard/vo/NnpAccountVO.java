package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpAccountVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accId;

	private NnpCountryVO nnpCountry;

	
	private EnvironmentVO4 env;

	@JsonIgnore
	private NnpUserRoleVO nnpRole;

	private String accName;

	private String accCategory;

	private String accStatus;

	private Long beginPort;

	private Long endPort;

	private String createdBy;

	private String modifiedBy;

	private ZonedDateTime createdOn;

	private ZonedDateTime modifiedOn;

	private Boolean active;

	private String organization;

	private String description;

}
