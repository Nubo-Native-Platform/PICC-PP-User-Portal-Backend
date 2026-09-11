package com.nnp.dashboard.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CHElementDetailVOV2 implements Serializable {
    private String chElementDtlId;

    private String elementDtlId;
    private String elementDtlName;
    private String elementDtlType;
    private String elementDtlHome;
    private String elementDtlDesc;
    private String elementDtlURL;
    private String elementDtlFatNo;
    private String demoUrl;
    private String homeIcon;
    private String elementDtlSeq;
    private boolean isAssigned = false;
}
