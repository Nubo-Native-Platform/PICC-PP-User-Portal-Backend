package com.nnp.dashboard.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ElementDetailVOV2 implements Serializable {
    private String elementDtlId;

    private String elementId;

    private String elementDtlName;
    private String elementDtlType;
    private String elementDtlHome;
    private String elementDtlDesc;
    private String elementDtlURL;
    private String elementDtlFatNo;
    private String elementDtlSeq;
    private boolean isAssigned = false;
}
