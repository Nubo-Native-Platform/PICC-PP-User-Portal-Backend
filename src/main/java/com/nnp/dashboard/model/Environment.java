package com.nnp.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;



/**
 * @author AC
 *
 */

@Entity

@Table(name = "nnp_env")
@Getter @Setter 
public class Environment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8170748626342800441L;
	
	@Id
	@Column(name = "env_id", nullable = false)
	private String envId;

	@Column(name = "env_code", nullable = false)
	private String envCode;

	@Column(name = "env_name", nullable = false)
	private String envName;

	@Column(name = "env_type")
	private String envType;

	@Column(name = "env_custid", nullable = false)
	private String envCustId;

	@Column(name = "env_custname")
	private String envCustName;

	@Column(name = "env_desc")
	private String envDesc;

	@Column(name = "env_tenant", nullable = false)
	private String envTenantId;

	@Column(name = "env_fapid", nullable = false)
	private String envFapId;

	@Column(name = "env_fatno", nullable = false)
	private String envFatNo;

	@Column(name = "env_email")
	private String envEmail;
	
	@Column(name = "env_status")
	private String envStatus;

	@OneToMany( mappedBy = "env",  fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private List<EnvFeature> envFeatures = new ArrayList<EnvFeature>();
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "env_id")
	private List<EnvUserAccess> userList = new ArrayList<EnvUserAccess>();

}
