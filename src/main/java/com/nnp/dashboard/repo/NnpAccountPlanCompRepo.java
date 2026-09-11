package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpAccountPlanComp;

@Repository
public interface NnpAccountPlanCompRepo extends JpaRepository<NnpAccountPlanComp,String> {

	List<NnpAccountPlanComp> findByNnpAccountPlanAccPlanId(String accPlanId);
}
