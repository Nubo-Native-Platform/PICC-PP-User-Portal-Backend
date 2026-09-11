package com.nnp.dashboard.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ElementDetailVOV3 implements Serializable {
    private String elementDtlId;

    @JsonIgnore
    private FeatureElementVOV3 feaElement;

    private String elementDtlName;
    private String elementDtlType;
    private String elementDtlHome;
    private String elementDtlDesc;
    private String elementDtlURL;
    private String elementDtlFatNo;
    private String elementDtlSeq;
    private boolean isAssigned = false;
    private List<CHElementDetailVOV3> childElementDtls = new ArrayList<>();
}
