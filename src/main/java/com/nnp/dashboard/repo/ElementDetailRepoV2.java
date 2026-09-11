package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.ElementDetailV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElementDetailRepoV2 extends JpaRepository<ElementDetailV2, String> {

	public List<ElementDetailV2> findByElementId(String elementId);
}
