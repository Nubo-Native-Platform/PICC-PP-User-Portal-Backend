package com.nnp.dashboard.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nnp.dashboard.model.NnpUserRegister;

public interface NnpUserRegisterRepo extends JpaRepository<NnpUserRegister, String> {

	List<NnpUserRegister> findByEmail(String email);
	
	List<NnpUserRegister> findByContact(String contact);
}
