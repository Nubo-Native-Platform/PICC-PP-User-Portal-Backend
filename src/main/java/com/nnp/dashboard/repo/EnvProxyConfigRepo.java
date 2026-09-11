package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.EnvProxyConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvProxyConfigRepo extends JpaRepository<EnvProxyConfig, String> {
}
