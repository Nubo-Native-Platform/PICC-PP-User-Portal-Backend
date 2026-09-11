package com.nnp.dashboard.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class EnvFeatureVOV2 implements Serializable {
    private String feaId;

    private String envId;
    private String feaName;
    private String feaType;
    private String feaDesc;
    private String feaSeq;
    private boolean isAssigned = false;
}
