package com.nnp.dashboard.model;
import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_env_usage", schema = "portal")
@Getter
@Setter
public class NnpEnvUsage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usage_seq")
    @SequenceGenerator(name = "usage_seq", sequenceName = "env_generic_id_seq", allocationSize = 1)
    @Column(name = "usage_id", nullable = false)
    private Long usageId;

    @Column(name = "env_id", nullable = false)
    private String envId;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "hour")
    private Integer hour;

    @Column(name = "avg_cpu")
    private Integer avgCpu;

    @Column(name = "avg_mem")
    private Integer avgMem;

    @Column(name = "avg_storage")
    private Integer avgStorage;

    @Column(name = "avg_pod")
    private Integer avgPod;
    
    @Column(name = "max_cpu")
    private Integer maxCpu;

    @Column(name = "max_mem")
    private Integer maxMem;

    @Column(name = "max_storage")
    private Integer maxStorage;

    @Column(name = "max_pod")
    private Integer maxPod;
  
}
