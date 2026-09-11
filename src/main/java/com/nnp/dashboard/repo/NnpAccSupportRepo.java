package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.NnpAccSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface NnpAccSupportRepo extends JpaRepository<NnpAccSupport, String> {

    List<NnpAccSupport> findByNnpAccountAccId(String accId);

    @Query("""
                SELECT s FROM NnpAccSupport s
                WHERE (:accName IS NULL OR s.nnpAccount.accName = :accName)
                AND s.status IN :statuses
            """)
    Page<NnpAccSupport> findByAccIdAndIssueStatus(
            @Param("statuses") List<String> statuses,
            @Param("accName") String accName,
            Pageable pageable
    );

    List<NnpAccSupport> findByTktStartAfterAndNnpAccount_AccName(ZonedDateTime tktStart, String accName);

}
