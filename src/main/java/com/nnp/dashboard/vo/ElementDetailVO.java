package com.nnp.dashboard.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ElementDetailVO{
	private String elementDtlId;

	@JsonIgnore
	private FeatureElementVO feaElement;

	private String elementDtlName;
	private String elementDtlType;
	private String elementDtlHome;
	private String elementDtlDesc;
	private String elementDtlURL;
	private String elementDtlFatNo;
	private String elementDtlSeq;
	private boolean isAssigned = false;

	private List<CHElementDetailVO> childElementDtls = new ArrayList<>();

}
