package com.nnp.dashboard.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "nnp_plan_comp", schema = "portal")
@Getter
@Setter
public class NnpPlanComp implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "host_regplid", nullable = false)
	private Long planCompId;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "env_compid")
	private EnvBbComp envBbComp;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "host_plid")
	private NnpPlan nnpPlan;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comp_group_id")
	private NNPPlanCompGroup planCompGroup;

	@Column(name = "host_plcmptype")
	private String placementType;

	@Column(name = "host_basedaypr")
	private String basePrice;

}
