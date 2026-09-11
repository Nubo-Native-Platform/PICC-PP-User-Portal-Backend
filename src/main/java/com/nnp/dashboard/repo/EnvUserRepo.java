package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nnp.dashboard.model.EnvUserAccess;

public interface EnvUserRepo extends JpaRepository<EnvUserAccess, String> {
	
	public List<EnvUserAccess> findByUserId(String userId);
	
	List<EnvUserAccess> findByUserIdAndEnvIdAndChElmDetailIdIsNotNull(String userId, String envId);
	
	public List<EnvUserAccess> findByUserIdAndChElmDetailIdIsNotNull(String userId);

	

}
