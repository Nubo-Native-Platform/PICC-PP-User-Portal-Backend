package com.nnp.dashboard.repo.apiecosystem;

import com.nnp.dashboard.dto.ApigatwayStatsDto;
import com.nnp.dashboard.model.apiecosystem.ApiTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApiTransactionRepo extends JpaRepository<ApiTransaction, Long> {
    @Query(value = """
        SELECT 
            t.env_code AS envCode,
            AVG(EXTRACT(EPOCH FROM (t.call_return_ts - t.call_receive_ts))) AS avgRespTime,
            SUM(t.req_size) AS reqSize,
            SUM(t.resp_size) AS respSize,
            SUM(CASE WHEN t.success = true THEN 1 ELSE 0 END) AS successCount,
            SUM(CASE WHEN t.success = false THEN 1 ELSE 0 END) AS failCount
        FROM apiecosystem.api_transaction t
        JOIN apiecosystem.api_registry a ON t.apiid = a.apiid
        WHERE t.call_receive_ts >= (NOW() AT TIME ZONE 'UTC') - INTERVAL '1 hour'
        GROUP BY t.env_code
        """,
            nativeQuery = true)
    List<ApigatwayStatsDto> getApigatewayStatsLastHour();
}
