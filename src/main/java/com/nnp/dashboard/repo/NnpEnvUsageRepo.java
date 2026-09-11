package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nnp.dashboard.model.NnpEnvUsage;

public interface NnpEnvUsageRepo extends JpaRepository<NnpEnvUsage, Long> {

	List<NnpEnvUsage> findByEnvId(String envId);

	List<NnpEnvUsage> findTop24ByEnvIdOrderByUsageDateDescHourDesc(String envId);

	@Query(value = """
		SELECT ROW_NUMBER() OVER (ORDER BY usage_date DESC, hour DESC) AS usage_id, '' AS env_id, usage_date, hour, AVG(avg_cpu) AS avg_cpu, AVG(avg_mem) AS avg_mem, AVG(avg_storage) AS avg_storage, 0 AS avg_pod, MAX(max_cpu) AS max_cpu, MAX(max_mem) AS max_mem, MAX(max_storage) AS max_storage, 0 AS max_pod
		FROM nnp_env_usage u
		GROUP BY u.usage_date, u.hour
		ORDER BY u.usage_date DESC, u.hour DESC
		LIMIT 24
	""", nativeQuery = true)
	List<NnpEnvUsage> findTop24HourlyUsage();

}
