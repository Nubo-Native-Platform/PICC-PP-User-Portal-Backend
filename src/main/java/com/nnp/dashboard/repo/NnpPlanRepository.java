package com.nnp.dashboard.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nnp.dashboard.model.NnpPlan;

@Repository
public interface NnpPlanRepository extends JpaRepository<NnpPlan, String> {
	
	List<NnpPlan> findByNnpCountryCountryId(String countryId);

	List<NnpPlan> findByNnpCountry_CountryCode(String countryCode);

}
