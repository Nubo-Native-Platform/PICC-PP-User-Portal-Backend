package com.nnp.dashboard.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpEnvPipeline;

@Repository
public interface NnpEnvPipelineRepo extends JpaRepository<NnpEnvPipeline, Long> {

	List<NnpEnvPipeline> findByEnvId(String envId);
	
	@Query("SELECT p.plId as plId, p.envId AS envId, p.plDate AS plDate, p.hour AS hour, p.avgPlTime AS avgPlTime, p.plSuccess AS plSuccess, p.plFail AS plFail " +
		       "FROM NnpEnvPipeline p " +
		       "WHERE p.envId = :envId " +
		       "ORDER BY p.plDate DESC, p.hour DESC "+
			   "LIMIT 24")
	List<Map<String, Object>> getLast24PipelineExecutionsStatsByEnvId(@Param("envId") String envId);

	@Query("SELECT '' as plId, 'ALL' AS envId, p.plDate AS plDate, p.hour AS hour, AVG(p.avgPlTime) AS avgPlTime, SUM(p.plSuccess) AS plSuccess, SUM(p.plFail) AS plFail " +
			"FROM NnpEnvPipeline p " +
			"GROUP BY p.plDate, p.hour " +
			"ORDER BY p.plDate DESC, p.hour DESC "+
			"LIMIT 24")
	List<Map<String, Object>> getLast24PipelineExecutionsStatsForAllEnv();

}
