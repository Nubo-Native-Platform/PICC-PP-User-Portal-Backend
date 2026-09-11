package com.nnp.dashboard.repo;

import com.nnp.dashboard.model.UserConfigV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface UserConfigRepoV2 extends JpaRepository<UserConfigV2, String> {

	List<UserConfigV2> findByChElmDetail_ElementDtlIdAndUserId(String elementDtlId, String userId);

	List<UserConfigV2> findByUserIdAndEnvIdAndChElmDetail_ChElementDtlIdIsNotNull(String userId, String envId);
}
