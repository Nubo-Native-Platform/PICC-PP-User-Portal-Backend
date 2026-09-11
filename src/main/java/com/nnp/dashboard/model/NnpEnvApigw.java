package com.nnp.dashboard.model;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_env_apigw", schema = "portal")
@Getter
@Setter
public class NnpEnvApigw implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "apigw_seq")
	@SequenceGenerator(name = "apigw_seq", sequenceName = "env_generic_id_seq", allocationSize = 1)
	@Column(name = "apigw_id", nullable = false)
	private Long apigwId;

	@Column(name = "env_id", nullable = false)
	private String envId;

	@Column(name = "api_date", nullable = false)
	private LocalDate apiDate;

	@Column(name = "hour")
	private Integer hour;

	@Column(name = "avg_resp_time")
	private Long avgRespTime;

	@Column(name = "tot_tran_vol")
	private Long totTranVol;

	@Column(name = "api_success")
	private Integer apiSuccess;

	@Column(name = "api_fail")
	private Integer apiFail;

}
