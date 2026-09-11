package com.nnp.dashboard.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpUserRoleVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String roleId;

	private String roleName;

	private String roleDesc;

	private String roleStat;
	
	@JsonIgnore
	private List<NnpAccountVO> accounts=new ArrayList<NnpAccountVO>();

}
