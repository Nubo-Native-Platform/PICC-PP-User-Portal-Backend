package com.nnp.dashboard.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CHElementDetailVOV3 implements Serializable {
    private String chElementDtlId;

    @JsonIgnore
    private ElementDetailVOV3 prElemDtl;
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
