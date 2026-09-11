
package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.EnvironmentV3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author AC
 *
 */
@Repository
public interface EnvironmentRepoV3 extends JpaRepository<EnvironmentV3, String> {

	
	public EnvironmentV3 findByEnvCode(String envCode);

	public boolean existsByEnvCode(String envCode);

	

}
