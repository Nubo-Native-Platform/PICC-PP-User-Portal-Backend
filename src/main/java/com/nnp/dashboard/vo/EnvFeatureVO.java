package com.nnp.dashboard.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EnvFeatureVO {
	private String feaId;
	
	@JsonIgnore
	private EnvironmentVO env;
	private String feaName;
	private String feaType;
	private String feaDesc;
	private String feaSeq;
	private boolean isAssigned = false;
	private List<FeatureElementVO> featureElements = new ArrayList<FeatureElementVO>();
	
	
}
