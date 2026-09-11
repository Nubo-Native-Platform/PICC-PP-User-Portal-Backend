package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.FeatureElementV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeatureElementRepoV2 extends JpaRepository<FeatureElementV2, String> {

	public List<FeatureElementV2> findByFeatureId(String featureId);
}
