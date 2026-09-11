package com.nnp.dashboard.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_account", schema = "portal")
@Getter
@Setter
public class NnpAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "acc_id")
	private String accId;

	@SuppressWarnings("deprecation")
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,optional = false)
	@JoinColumn(name = "country_id")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	private NnpCountry nnpCountry;

	@SuppressWarnings("deprecation")
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,optional =false)
	@JoinColumn(name = "env_id")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	private EnvironmentV4 env;

	/*@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,optional = false)
	@JoinColumn(name = "role_id")
	private NnpUserRole userRole;*/

	@Column(name = "acc_name")
	private String accName;

	@Column(name = "acc_category", length = 255)
	private String accCategory;

	@Column(name = "acc_status", length = 255)
	private String accStatus;

	/*@Column(name = "begin_port")
	private Long beginPort;

	@Column(name = "end_port")
	private Long endPort;*/

	@Column(name = "created_by", length = 255)
	private String createdBy;

	@Column(name = "modified_by", length = 255)
	private String modifiedBy;

	@Column(name = "created_on")
	private ZonedDateTime createdOn;

	@Column(name = "modified_on")
	private ZonedDateTime modifiedOn;

	private String organization;

	private String description;

	/*@Column(name = "active")
	private Boolean active;*/
	
	@OneToMany(mappedBy = "nnpAccount",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpAccDcnt> nnpAccDcnts=new ArrayList<NnpAccDcnt>();
	
	@OneToMany(mappedBy = "nnpAccount",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpAccBill> accBills=new ArrayList<NnpAccBill>();
	
	@OneToMany(mappedBy = "nnpAccount",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpAccountPlan> accountPlans=new ArrayList<NnpAccountPlan>();
	
	@OneToMany(mappedBy = "nnpAccount",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpAccComm> accComms=new ArrayList<NnpAccComm>();
	
	@OneToMany(mappedBy = "nnpAccount",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpAccSupport> accSupports=new ArrayList<NnpAccSupport>();

}
