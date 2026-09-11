package com.nnp.dashboard.vo;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserAccessVO {
	private String userAccountId;
	private String userId;
	private String userEmail;
	private String contactNo;
	private String password;
	
	/*
	 * private List<EnvironmentVO> envList = new ArrayList<>(); private
	 * List<EnvFeatureVO> featureList = new ArrayList<>(); private
	 * List<FeatureElementVO> feaElemList = new ArrayList<>(); private
	 * List<ElementDetailVO> elemDeatilId = new ArrayList<>();
	 */
	@Override
	public boolean equals(Object obj) {
		
		return this.userId.equalsIgnoreCase(((UserAccessVO)obj).userId);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.userId.hashCode();
	}
	
	
}
