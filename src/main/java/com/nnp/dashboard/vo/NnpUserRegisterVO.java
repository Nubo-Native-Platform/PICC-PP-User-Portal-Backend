package com.nnp.dashboard.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NnpUserRegisterVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;

	private String envId;

	private String roleId;

	private String firstName;

	private String lastName;

	private String email;

	private String contact;

	private String updateComment;

	private String userType;

	private String userStatus;

	private String envParam1;

	private String envParam2;

	private String envParam3;

	private String envParam4;//temporary password capture

}
