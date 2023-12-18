package com.jsp.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jsp.ecommerce.dto.Customerdto;
import com.jsp.ecommerce.service.CustomerService;

import jakarta.validation.Valid;

@RequestMapping("/customer")
@Controller
public class CustomerController {

	@Autowired
	Customerdto customerdto;

	@Autowired
	CustomerService customerService;

	@GetMapping("/signup")
	public String loadSignup(ModelMap map) {
		map.put("customerdto", customerdto);
		return "Signup";
	}

	
	
	@PostMapping("/signup")
	public String signup(@Valid Customerdto customerdto, BindingResult result, ModelMap map) {
		if (result.hasErrors()) {
			return "Signup";
		} else {
			return customerService.signup(customerdto, map);

		}
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam int otp,@RequestParam int id,ModelMap map) {
		return customerService.verifyOtp(id,otp,map);
		
	}
	
	

}
