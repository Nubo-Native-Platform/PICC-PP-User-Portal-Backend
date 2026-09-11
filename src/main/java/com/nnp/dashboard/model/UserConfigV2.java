package com.nnp.dashboard.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "nnp_env_user_access")
@Getter @Setter
public class UserConfigV2 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id

	@Column(name = "usracc_id", nullable = false)
	private String userAccessId;

	@Column(name = "user_id", nullable = false)
	private String userId;
	@Column(name = "env_id", nullable = false)
	private String envId;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "dtlspec_id", referencedColumnName = "dtlspec_id")
	private CHElementDetailV2 chElmDetail;

}
