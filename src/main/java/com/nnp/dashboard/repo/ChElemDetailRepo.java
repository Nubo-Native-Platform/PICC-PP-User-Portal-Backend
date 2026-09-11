package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nnp.dashboard.model.CHElementDetail;
import com.nnp.dashboard.model.ElementDetail;

/**
 * @author AC
 *
 */
public interface ChElemDetailRepo extends JpaRepository<CHElementDetail, String> {

	public List<CHElementDetail> findByPrElemDtlAndUserList_UserId(ElementDetail prElemDtl,String userId);
	
	public CHElementDetail findByChElementDtlIdAndElementDtlHome(String chElementDtlId,String elementDtlHome);
	
	public CHElementDetail findByChElementDtlId(String chElementDtlId);

}
