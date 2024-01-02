package com.jsp.ecommerce.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jsp.ecommerce.dto.Item;
import com.jsp.ecommerce.dto.Productdto;
import com.jsp.ecommerce.repository.ItemRepository;
import com.jsp.ecommerce.repository.ProductRepository;

@Repository
public class Productdao {

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ItemRepository itemRepository;

	public void save(Productdto productdto) {
		productRepository.save(productdto);

	}

	public List<Productdto> fetchAll() {
		return productRepository.findAll();

	}

	public Productdto findById(int id) {
		return productRepository.findById(id).orElse(null);
	}

	public void delete(Productdto productdto) {
		productRepository.delete(productdto);

	}

	public List<Productdto> fetchDisplayProducts() {
		return productRepository.findByDisplayTrue();

	}

	public void deleteItem(Item item) {
		itemRepository.delete(item);
		
	}

	

}
