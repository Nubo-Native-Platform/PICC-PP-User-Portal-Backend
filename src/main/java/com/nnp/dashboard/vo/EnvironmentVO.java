package com.nnp.dashboard.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class EnvironmentVO {

	private String envId;
	private String envCode;
	private String envName;
	private String envType;
	private String envCustId;
	private String envCustName;
	private String envDesc;
	private String envTenantId;
	private String envFapId;
	private String envFatNo;
	private String envEmail;
	private String envEmailServerIp;
	private String envEmailServerPort;
	private String envStatus;
	
	private List<EnvFeatureVO> envFeatures = new ArrayList<EnvFeatureVO>();
	
	private Set<UserAccessVO> userList = new HashSet<UserAccessVO>();
	
}
