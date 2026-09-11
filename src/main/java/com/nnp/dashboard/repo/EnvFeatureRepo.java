package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nnp.dashboard.model.EnvFeature;
import com.nnp.dashboard.model.Environment;
/**
 * @author AC
 *
 */
public interface EnvFeatureRepo extends JpaRepository<EnvFeature, String>{

	public List<EnvFeature> findByEnvAndUserList_UserIdOrderByFeaSeqAsc(Environment env,String userId);
}
