package com.nnp.dashboard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nnp_user_role", schema = "portal")
@Getter
@Setter
public class NnpUserRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "role_id")
	private String roleId;

	@Column(name = "role_name")
	private String roleName;

	@Column(name = "role_desc")
	private String roleDesc;

	@Column(name = "role_stat")
	private String roleStat;
	
	/*@OneToMany(mappedBy = "userRole",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpAccount> accounts=new ArrayList<NnpAccount>();*/
	
	@OneToMany(mappedBy = "userRole",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	private List<NnpUser> nnpUserRole=new ArrayList<NnpUser>();

}
