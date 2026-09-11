package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpPlanComp;

@Repository
public interface NnpPlanCompRepo extends JpaRepository<NnpPlanComp,String> {

}
