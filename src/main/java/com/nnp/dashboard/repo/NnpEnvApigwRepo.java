package com.nnp.dashboard.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpEnvApigw;

@Repository
public interface NnpEnvApigwRepo extends JpaRepository<NnpEnvApigw, Long> {

	List<NnpEnvApigw> findByEnvId(String envId);

	@Query("SELECT p.apigwId as apigwId, p.envId AS envId, p.apiDate AS apiDate, p.hour AS hour, p.avgRespTime AS avgRespTime, p.totTranVol AS totTranVol, "
			+ "p.apiSuccess AS apiSuccess, p.apiFail AS apiFail "
			+ "FROM NnpEnvApigw p " + "WHERE p.envId = :envId "
			+ "ORDER BY p.apiDate DESC, p.hour DESC "
			+ "LIMIT 24")
	List<Map<String, Object>> findApiGatewayResponseDetailsForLast24HoursByEnvId(@Param("envId") String envId);

	@Query("SELECT '' as apigwId, 'ALL' AS envId, p.apiDate AS apiDate, p.hour AS hour, AVG(p.avgRespTime) AS avgRespTime, SUM(p.totTranVol) AS totTranVol, "
			+ "SUM(p.apiSuccess) AS apiSuccess, SUM(p.apiFail) AS apiFail "
			+ "FROM NnpEnvApigw p "
			+ "GROUP BY p.apiDate, p.hour "
			+ "ORDER BY p.apiDate DESC, p.hour DESC "
			+ "LIMIT 24")
	List<Map<String, Object>> findApiGatewayResponseDetailsForLast24HoursForAllEnv();
}
