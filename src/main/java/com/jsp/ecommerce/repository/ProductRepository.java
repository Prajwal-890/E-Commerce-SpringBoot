package com.jsp.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.ecommerce.dto.Productdto;

public interface ProductRepository extends JpaRepository<Productdto, Integer>{
	
}
