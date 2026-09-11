package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nnp.dashboard.model.NnpUser;

public interface NnpUserRepo extends JpaRepository<NnpUser, String> {


	@Query("SELECT u.userId as userId,u.nnpEnv.envId as envId, u.userRole.roleId as roleId,u.firstName as firstName,u.lastName as lastName,u.email as email,u.contact as contact,u.requestDate as requestDate,u.userType as userType,u.userStatus as userStatus FROM NnpUser u WHERE u.nnpEnv.envId = :envId")
	List<Object[]> findByNnpEnvEnvId(String envId);

	
}
