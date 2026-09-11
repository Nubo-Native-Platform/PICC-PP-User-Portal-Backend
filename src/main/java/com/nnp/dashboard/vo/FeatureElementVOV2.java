package com.nnp.dashboard.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FeatureElementVOV2 implements Serializable {
    private String elementId;

    private String featureId;
    private String elementName;
    private String elementType;
    private String elementDesc;
    private String elementPage;
    private String feaSeq;
    private boolean isAssigned = false;
}
