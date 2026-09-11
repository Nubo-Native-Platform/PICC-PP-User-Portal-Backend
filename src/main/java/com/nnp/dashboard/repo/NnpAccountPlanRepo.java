package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpAccountPlan;

@Repository
public interface NnpAccountPlanRepo extends JpaRepository<NnpAccountPlan, String> {
	
	List<NnpAccountPlan> findByNnpPlanPlanId(Long planId);

}
