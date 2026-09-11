package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpAccDcnt;

@Repository
public interface NnpAccDcntRepo extends JpaRepository<NnpAccDcnt, String> {
	
	List<NnpAccDcnt> findByNnpAccountAccId(String accId);

}
