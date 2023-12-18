package com.jsp.ecommerce.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jsp.ecommerce.dto.Customerdto;
import com.jsp.ecommerce.repository.CustomerRepository;

@Repository
public class Customerdao {

	@Autowired
	CustomerRepository customerRepository;

	public List<Customerdto> findByEmailOrMobile(String email, long mobile) {
		return customerRepository.findByEmailOrMobile(email, mobile);
	}

	public void save(Customerdto customerdto) {
		customerRepository.save(customerdto);
	}

	public Customerdto findById(int id) {
		return customerRepository.findById(id).orElse(null);
	}

	public void update(Customerdto customerdto) {
		customerRepository.save(customerdto);

	}

}
