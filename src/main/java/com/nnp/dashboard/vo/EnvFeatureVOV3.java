package com.nnp.dashboard.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EnvFeatureVOV3 implements Serializable {
    private String feaId;

    @JsonIgnore
    private EnvironmentVOV3 env;

    private String feaName;
    private String feaType;
    private String feaDesc;
    private String feaSeq;
    private boolean isAssigned = false;

	private List<FeatureElementVOV3> featureElements = new ArrayList<>();
}
