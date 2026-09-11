package com.nnp.dashboard.vo;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EnvironmentVOV3 implements Serializable {

	private String envId;
	private String envCode;
	private String envName;
	private String envTypeId;
	private String envCustId;
	private String envCustName;
	private String envDesc;
	private String envTenantId;
	private String envFapId;
	private String envFatNo;
	private String envEmail;
	private String envStatus;
	private String envNamespace;
	private String envDomain;
	private String envRepo;
	private String envIp;
	private String adminK8sNsToken;
	private String userK8sNsToken;
	
	private List<EnvFeatureVOV3> envFeatures = new ArrayList<>();
}
