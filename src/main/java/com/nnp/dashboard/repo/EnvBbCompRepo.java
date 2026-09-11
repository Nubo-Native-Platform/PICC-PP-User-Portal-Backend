package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.EnvBbComp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvBbCompRepo extends JpaRepository<EnvBbComp, String> {
    boolean existsByEnvCompId(String envCompId);
}
