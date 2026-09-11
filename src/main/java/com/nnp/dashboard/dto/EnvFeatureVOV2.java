package com.nnp.dashboard.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EnvFeatureVOV2 implements Serializable {

	private static final long serialVersionUID = 1L;

	private String feaId;
	private String envId;
	private String feaName;
	private String feaType;
	private String feaDesc;
	private String feaSeq;
	private boolean isAssigned = false;
	private List<FeatureElementVOV2> featureElements = new ArrayList<FeatureElementVOV2>();

}
