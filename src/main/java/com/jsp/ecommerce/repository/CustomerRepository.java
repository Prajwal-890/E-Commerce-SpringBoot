package com.jsp.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jsp.ecommerce.dto.Customerdto;


public interface CustomerRepository extends JpaRepository<Customerdto, Integer>{

	
	List<Customerdto> findByEmailOrMobile(String email, long mobile);
	
	
}
