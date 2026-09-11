package com.nnp.dashboard.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpAccount;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface NnpAccountRepository extends JpaRepository<NnpAccount, String> {

	List<NnpAccount> findByEnvEnvId(String envId);
	Optional<NnpAccount> findByAccName(String accountName);

	@EntityGraph(attributePaths = {"nnpCountry", "env"})
	Page<NnpAccount> findByAccStatusOrderByCreatedOnDesc(String accStatus, Pageable pageable);

	@EntityGraph(attributePaths = {"nnpCountry", "env"})
	Page<NnpAccount> findAll(Pageable pageable);
}