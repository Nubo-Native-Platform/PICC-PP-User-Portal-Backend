package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvironmentVO4 implements Serializable {

	private static final long serialVersionUID =1L;

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

	private String envStatus;

	@JsonIgnore
	private List<NnpAccountVO> nnpAccounts=new ArrayList<NnpAccountVO>(); 

	

}
