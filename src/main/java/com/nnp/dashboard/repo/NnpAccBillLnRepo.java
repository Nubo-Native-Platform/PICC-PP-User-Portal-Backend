package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpAccBillLn;

@Repository
public interface NnpAccBillLnRepo extends JpaRepository<NnpAccBillLn, String> {
	
	List<NnpAccBillLn> findByAccBillAccBillId(String accBillId);

}
