package com.nnp.dashboard.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_user", schema = "portal")
@Getter
@Setter
public class NnpUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "user_id", nullable = false)
	private String userId;

	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,optional =false)
	@JoinColumn(name = "env_id")
	private EnvironmentV4 nnpEnv;
	
	@Column(name = "acc_id", nullable = false)
	private String accId;

	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,optional = false)
	@JoinColumn(name = "role_id	")
	private NnpUserRole userRole;

	@Column(name = "f_name")
	private String firstName;

	@Column(name = "l_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "contact")
	private String contact;

	@Column(name = "req_dt")
	private OffsetDateTime requestDate;

	@Column(name = "udp_dt")
	private OffsetDateTime updateDate;

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

	@Column(name = "env_param4")//temporary Password capture
	private String envParam4;

	private String address;

	public String getFullName(){
		return this.firstName + " " + this.lastName;
	}

}
