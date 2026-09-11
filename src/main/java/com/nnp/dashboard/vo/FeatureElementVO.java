package com.nnp.dashboard.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class FeatureElementVO {
	private String elementId;
	@JsonIgnore
	private EnvFeatureVO feature;
	private String elementName;
	private String elementType;
	private String elementDesc;
	private String elementPage;
	private String feaSeq;
	private boolean isAssigned = false;
	private List<ElementDetailVO> elementDetails = new ArrayList<ElementDetailVO>();	

}
