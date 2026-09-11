package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.CHElementDetailV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChElemDetailRepoV2 extends JpaRepository<CHElementDetailV2, String> {

	public List<CHElementDetailV2> findByElementDtlId(String elementDtlId);

	public CHElementDetailV2 findByChElementDtlIdAndElementDtlHome(String chElementDtlId, String elementDtlHome);

	@Query("SELECT u FROM CHElementDetailV2 u WHERE u.chElementDtlId =:chElementDtlId AND (u.elementDtlURL IS NOT NULL OR u.demoUrl IS NOT NULL)")
	public Optional<CHElementDetailV2> findByChElementDtlIdAndElementDtlURLOrDemoUrlIsNotNull(@Param("chElementDtlId") String chElementDtlId);
}
