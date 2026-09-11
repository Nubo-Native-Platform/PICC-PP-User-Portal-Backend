package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpAccBillCol;

@Repository
public interface NnpAccBillColRepo extends JpaRepository<NnpAccBillCol, String> {

	List<NnpAccBillCol> findByAccBillAccBillId(String accBillId);
}
