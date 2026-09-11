package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.EnvFeatureV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnvFeatureRepoV2 extends JpaRepository<EnvFeatureV2, String> {
    public List<EnvFeatureV2> findByEnvId(String envId);
}
