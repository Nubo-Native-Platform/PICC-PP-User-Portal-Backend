
package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.EnvironmentV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author AC
 *
 */
@Repository
public interface EnvironmentRepoV2 extends JpaRepository<EnvironmentV2, String> {

	
	public EnvironmentV2 findByEnvCode(String envCode);

	

}
