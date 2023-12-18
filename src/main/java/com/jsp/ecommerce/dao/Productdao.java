package com.jsp.ecommerce.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jsp.ecommerce.dto.Productdto;
import com.jsp.ecommerce.repository.ProductRepository;

@Repository
public class Productdao {
	
	@Autowired
	ProductRepository productRepository;

	public void save(Productdto productdto) {
		productRepository.save(productdto);
		
	}

	public List<Productdto> fetchAll() {
		return productRepository.findAll();
		
	}	
	
	
}
