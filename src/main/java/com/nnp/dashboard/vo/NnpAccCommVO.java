package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpAccCommVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accCommId;
	
	@JsonIgnore
	private NnpAccountVO nnpAccount;

	private String commType;

	private ZonedDateTime commDate;

	private String commCategory;

	private String commTitle;

	private String commDescription;

	private String commFileLinks;

	private String commComments;

	private String createdBy;

	private String modifiedBy;

	private ZonedDateTime createdOn;

	private ZonedDateTime modifiedOn;

}
