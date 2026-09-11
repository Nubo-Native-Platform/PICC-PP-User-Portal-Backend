package com.nnp.dashboard.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.nnp.dashboard.vo.ElementDetailVOV2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FeatureElementVOV2 implements Serializable {

	private static final long serialVersionUID = 1L;

	private String elementId;
    private String featureId;
    private String elementName;
    private String elementType;
    private String elementDesc;
    private String elementPage;
    private String feaSeq;
    private boolean isAssigned = false;
    private List<ElementDetailVOV2> elementDetails = new ArrayList<ElementDetailVOV2>();

}
