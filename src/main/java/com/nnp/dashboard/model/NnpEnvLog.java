package com.nnp.dashboard.model;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_env_log", schema = "portal")
@Getter
@Setter
public class NnpEnvLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_seq")
    @SequenceGenerator(name = "log_seq", sequenceName = "env_generic_id_seq", allocationSize = 1)
    @Column(name = "log_id", nullable = false)
    private Long logId;

    @Column(name = "env_id", nullable = false)
    private String envId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "hour")
    private Integer hour;

    @Column(name = "app_name", length = 200)
    private String appName;

    @Column(name = "tot_err")
    private Integer totErr;

    @Column(name = "tot_msg")
    private Integer totMsg;
  
}
