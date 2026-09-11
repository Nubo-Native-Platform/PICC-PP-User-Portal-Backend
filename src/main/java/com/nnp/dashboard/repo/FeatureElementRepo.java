package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nnp.dashboard.model.EnvFeature;
import com.nnp.dashboard.model.FeatureElement;
/**
 * @author AC
 *
 */
public interface FeatureElementRepo extends JpaRepository<FeatureElement, String> {

	public List<FeatureElement> findByFeatureAndUserList_UserId(EnvFeature feature,String userId);
}
