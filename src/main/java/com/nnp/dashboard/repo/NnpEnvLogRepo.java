package com.nnp.dashboard.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpEnvLog;

@Repository
public interface NnpEnvLogRepo extends JpaRepository<NnpEnvLog, Long> {

	List<NnpEnvLog> findByEnvId(String envId);

	List<NnpEnvLog> findTop24ByEnvIdOrderByLogDateDescHourDesc(String envId);

	@Query(value = """
		SELECT ROW_NUMBER() OVER (ORDER BY log_date DESC, hour DESC) AS log_id,'ALL_APPS' AS app_name, '' AS env_id, log_date, hour, SUM(tot_err) AS tot_err, SUM(tot_msg) as tot_msg
		FROM nnp_env_log l
		GROUP BY l.log_date, l.hour
		ORDER BY l.log_date DESC, l.hour DESC
		LIMIT 24
	""", nativeQuery = true)
	List<NnpEnvLog> findTop24HourlyLogs();

}
