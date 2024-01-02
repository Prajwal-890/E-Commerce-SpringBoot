package com.jsp.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.jsp.ecommerce.dto.Customerdto;
import com.jsp.ecommerce.service.CustomerService;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;
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

	@GetMapping("/home")
	public String loadHome(HttpSession session, ModelMap map) {
		if (session.getAttribute("customer") != null) {
			return "CustomerHome";
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@GetMapping("/fetch-products")
	public String fetchProducts(HttpSession session, ModelMap map) {
		Customerdto customerdto = (Customerdto) session.getAttribute("customer");
		if (customerdto != null) {
			return customerService.fetchProducts(map, customerdto);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}


	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam int otp, @RequestParam int id, ModelMap map) {
		return customerService.verifyOtp(id, otp, map);

	}

	@GetMapping("/cart-add/{id}")
	public String addToCart(HttpSession session, ModelMap map, @PathVariable int id) {
		Customerdto customerdto = (Customerdto) session.getAttribute("customer");
		if (customerdto != null) {
			return customerService.addToCart(customerdto, id, session, map);
		} else {
			map.put("fail", "Session Expired Login Again");
			return "Home";
		}

	}

	@GetMapping("/fetch-cart")
	public String viewCart(HttpSession session, ModelMap map) {
		Customerdto customerdto = (Customerdto) session.getAttribute("customer");
		if (customerdto != null) {
			return customerService.viewCart(customerdto, map);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}

	@GetMapping("/cart-remove/{id}")
	public String removeFromCart(HttpSession session, ModelMap map, @PathVariable int id) {
		Customerdto customerdto = (Customerdto) session.getAttribute("customer");
		if (customerdto != null) {
			return customerService.removeFromCart(customerdto, id, map, session);
		} else {
			map.put("fail", "Session Expired, Login Again");
			return "Home";
		}
	}
	
	@GetMapping("/payment")
	public String createOrder(HttpSession session,ModelMap map) throws RazorpayException {
		Customerdto customerdto = (Customerdto) session.getAttribute("customer");
		if (customerdto != null) {
			return customerService.createOrder(customerdto,map);
		}else {
			map.put("fail", "session expired login again");
			return "Home";
		}
		
	}

}
