package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpAccComm;

@Repository
public interface NnpAccCommRepo extends JpaRepository<NnpAccComm, String> {

	List<NnpAccComm> findByNnpAccountAccId(String accId);

}
