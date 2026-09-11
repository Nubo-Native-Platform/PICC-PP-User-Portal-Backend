package com.nnp.dashboard.vo;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserConfigVO extends UserAccessVO{
	private String envId;
	private String feaId;
	private String elemId;
	private String elmDetailId;
	private String chElmDetailId;

}
