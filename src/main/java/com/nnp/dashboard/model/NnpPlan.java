package com.nnp.dashboard.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
@Table(name = "nnp_plan", schema = "portal")
@Getter
@Setter
public class NnpPlan implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "host_planid")
	private Long planId;

	@SuppressWarnings("deprecation")
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,optional = false)
	@JoinColumn(name = "country_id")
	@LazyToOne(LazyToOneOption.NO_PROXY)
	private NnpCountry nnpCountry;

	@Column(name = "host_plname", nullable = false)
	private String hostPlName;

	@Column(name = "host_pldesc", columnDefinition = "TEXT")
	private String hostPlDesc;

	@Column(name = "host_plcatagory", columnDefinition = "TEXT")
	private String hostPlCategory;

	@Column(name = "host_plstatus", columnDefinition = "TEXT")
	private String hostPlStatus;

	@Column(name = "host_plbasepr", columnDefinition = "TEXT")
	private String hostPlBasePr;

	@Column(name = "host_maxpod")
	private Short hostMaxPod;

	@Column(name = "host_maxbandw", precision = 19, scale = 2)
	private BigDecimal hostMaxBandw;

	@Column(name = "host_maxaction")
	private Integer hostMaxAction;

	@Column(name = "host_minduration")
	private Integer hostMinDuration;

	@Column(name = "item_seq")
	private Short itemSeq;

	@Column(name = "host_plspotlgt", columnDefinition = "TEXT")
	private String hostPlSpotlgt;

	@Column(name = "host_maxpct", columnDefinition = "TEXT")
	private String hostMaxPct;

	@Column(name = "host_node")
	private String hostNode;

	@Column(name = "host_cpu")
	private String hostCpu;

	@Column(name = "host_mem")
	private String hostMem;

	@Column(name = "host_storage")
	private String hostStorage;

	@Column(name = "host_plandtl_pagelink", columnDefinition = "TEXT")
	private String hostPlanDtlPageLink;

	@Column(name = "active", nullable = false)
	private Boolean active = true;

	@Column(name = "host_default_dct")
	private BigDecimal hostDefaultDct;

	@OneToMany(mappedBy = "nnpPlan",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpAccountPlan> accountPlans=new ArrayList<NnpAccountPlan>();
	
	@OneToMany(mappedBy = "nnpPlan",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpPlanComp> planComps=new ArrayList<NnpPlanComp>();

}
