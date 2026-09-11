package com.nnp.dashboard.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_user", schema = "portal")
@Getter
@Setter
public class NnpUserRegister implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "env_id")
	private String envId;

	@Column(name = "role_id	")
	private String roleId;
	
	@Column(name = "acc_id", nullable = false)
	private String accId;

	@Column(name = "f_name")
	private String firstName;

	@Column(name = "l_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "contact")
	private String contact;

	@Column(name = "req_dt", insertable = true, updatable = false)
	private Timestamp requestDate = new Timestamp(System.currentTimeMillis());

	@Column(name = "udp_dt", insertable = true, updatable = false)
	private Timestamp updateDate = new Timestamp(System.currentTimeMillis());

	@Column(name = "udp_comm")
	private String updateComment;

	@Column(name = "user_type")
	private String userType;

	@Column(name = "user_status")
	private String userStatus;

	@Column(name = "env_param1")
	private String envParam1;

	@Column(name = "env_param2")
	private String envParam2;

	@Column(name = "env_param3")
	private String envParam3;

	@Column(name = "env_param4")//temporary password capture
	private String envParam4;

}
