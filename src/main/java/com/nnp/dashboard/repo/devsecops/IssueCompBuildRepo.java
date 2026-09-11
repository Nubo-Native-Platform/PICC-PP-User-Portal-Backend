package com.nnp.dashboard.repo.devsecops;

import com.nnp.dashboard.dto.PipelineStatsDto;
import com.nnp.dashboard.model.devsecops.IssueCompBuild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueCompBuildRepo extends JpaRepository<IssueCompBuild, Long> {

    @Query(value = """
        SELECT
            git_group_root AS envId,
            AVG(EXTRACT(EPOCH FROM (finished_at - started_at))) AS avgPlTime,
            SUM(CASE WHEN status = 'success' THEN 1 ELSE 0 END) AS totalPlSuccess,
            SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) AS totalPlFail
        FROM devopscollector.issue_comp_build
        WHERE started_at >= (NOW() AT TIME ZONE 'UTC') - INTERVAL '1 hour'
        GROUP BY git_group_root
        """, nativeQuery = true)
    List<PipelineStatsDto> getPipelineStatsLastHour();

}
