package com.nnp.dashboard.model;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_env_pipeline", schema = "portal")
@Getter
@Setter
public class NnpEnvPipeline implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pipeline_seq")
	@SequenceGenerator(name = "pipeline_seq", sequenceName = "env_generic_id_seq", allocationSize = 1)
	@Column(name = "pl_id", nullable = false)
	private Long plId;

	@Column(name = "env_id", nullable = false)
	private String envId;

	@Column(name = "pl_date", nullable = false)
	private LocalDate plDate;

	@Column(name = "hour")
	private Integer hour;

	@Column(name = "avg_pl_time")
	private Integer avgPlTime;

	@Column(name = "pl_success")
	private Integer plSuccess;

	@Column(name = "pl_fail")
	private Integer plFail;
	
}
