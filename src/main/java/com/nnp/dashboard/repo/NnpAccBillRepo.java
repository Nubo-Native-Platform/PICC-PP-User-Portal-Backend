package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nnp.dashboard.model.NnpAccBill;

public interface NnpAccBillRepo extends JpaRepository<NnpAccBill,String> {
	
	List<NnpAccBill> findByNnpAccountAccId(String accId);

}
