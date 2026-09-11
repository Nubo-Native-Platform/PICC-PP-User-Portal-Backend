package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.NnpEnvFeaElemDtlSpec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NnpEnvFeaElemDtlSpecRepo extends JpaRepository<NnpEnvFeaElemDtlSpec, String> {
    boolean existsByDtlSpecId(String dtlSpecId);
}
