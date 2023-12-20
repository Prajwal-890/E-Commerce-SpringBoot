package com.jsp.ecommerce.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.jsp.ecommerce.dao.Productdao;
import com.jsp.ecommerce.dto.Productdto;

@Service
public class AdminService {

	@Autowired
	Productdao productdao;

	public String addProduct(Productdto productdto, MultipartFile pic, ModelMap map) throws IOException {
		byte[] picture = new byte[pic.getInputStream().available()];
		pic.getInputStream().read(picture);

		productdto.setPicture(picture);
		productdao.save(productdto);

		map.put("pass", "Product Added Success");
		return "AdminHome";
	}

	public String fetchProducts(ModelMap map) {
		List<Productdto> productdtos = productdao.fetchAll();
		if (productdtos.isEmpty()) {
			map.put("fail", "No Products Found");
			return "AdminHome";
		} else {
			map.put("products", productdtos);
			return "AdminViewProduct";
		}
	}

	public String changeStatus(int id, ModelMap map) {
		Productdto productdto = productdao.findById(id);
		if (productdto.isDisplay())
			productdto.setDisplay(false);
		else
			productdto.setDisplay(true);

		productdao.save(productdto);

		map.put("pass", "Status Update Success");
		return fetchProducts(map);
	}

	public String deleteProduct(int id, ModelMap map) {
		Productdto productdto = productdao.findById(id);
		productdao.delete(productdto);

		map.put("pass", "Status Update Success");
		return fetchProducts(map);

	}

	public String editProduct(int id, ModelMap map) {
		Productdto productdto = productdao.findById(id);
		map.put("product", productdto);
		return "EditProduct.html";
	}

	public String updateProduct(Productdto productdto, MultipartFile pic, ModelMap map) throws IOException {
		byte[] picture = new byte[pic.getInputStream().available()];
		pic.getInputStream().read(picture);

		if (picture.length == 0)
			productdto.setPicture(productdao.findById(productdto.getId()).getPicture());
		else
			productdto.setPicture(picture);
		productdao.save(productdto);

		map.put("pass", "Product Updated Success");
		return fetchProducts(map);

	}

}
