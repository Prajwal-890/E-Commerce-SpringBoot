package com.jsp.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.ecommerce.dto.Productdto;

public interface ProductRepository extends JpaRepository<Productdto, Integer>{

	List<Productdto> findByDisplayTrue();
	
	
}
