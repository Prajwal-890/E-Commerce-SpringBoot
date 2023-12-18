package com.jsp.ecommerce.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.jsp.ecommerce.dao.Customerdao;
import com.jsp.ecommerce.dto.Customerdto;
import com.jsp.ecommerce.helper.AES;
import com.jsp.ecommerce.helper.EmailLogic;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerService {

	@Autowired
	Customerdao customerdao;

	@Autowired
	EmailLogic emailLogic;

	public String signup(Customerdto customerdto, ModelMap map) {
		// to check Email and Mobile is Unique
		List<Customerdto> exCustomersdto = customerdao.findByEmailOrMobile(customerdto.getEmail(),customerdto.getMobile());
		if (!exCustomersdto.isEmpty()) {
			map.put("fail", "Account Already exists");
			return "SignUp";
		} else {
			int otp = new Random().nextInt(100000, 999999);
			customerdto.setOtp(otp);
			// Encrypt the password
			customerdto.setPassword(AES.encrypt(customerdto.getPassword(), "123"));
			customerdao.save(customerdto);
			// send otp to email
//			emailLogic.sendOtp(customerdto);
			map.put("id", customerdto.getId());
			return "EnterOtp";
		}

	}

	public String verifyOtp(int id, int otp, ModelMap map) {
		Customerdto customerdto = customerdao.findById(id);
		if (customerdto.getOtp() == otp) {
			customerdto.setVerifed(true);
			customerdao.update(customerdto);
			map.put("pass", "Account Created Succesfully");
			return "Login.html";
		} else {
			map.put("fail", "Invalid Otp, try Again");
			map.put("id", id);
			return "EnterOtp";
		}
	}

	public String login(String emph, String password, ModelMap map, HttpSession session) {
		if (emph.equals("admin") && password.equals("admin")) {
			session.setAttribute("admin", "admin");
			map.put("pass", "Admin Login Successful");
			return "AdminHome";
		} else {
			long mobile = 0;
			String email = null;

			try {
				mobile = Long.parseLong(emph);
			} catch (NumberFormatException e) {
				email = emph;
			}

			List<Customerdto> customerdtos = customerdao.findByEmailOrMobile(email, mobile);
			if (customerdtos.isEmpty()) {
				map.put("fail", "InValid Email or Password");
			} else {
				Customerdto customerdto = customerdtos.get(0);
				if (AES.decrypt(customerdto.getPassword(), "123").equals(password)) { // <-----------------------------
					if (customerdto.isVerifed()) {
						session.setAttribute("customer", customerdto);
						map.put("pass", "Login Success");
						return "CustomerHome";
					} else {
						int otp = new Random().nextInt(100000, 999999);
						customerdto.setOtp(otp);
						// Encrypt the password
						customerdto.setPassword(AES.encrypt(customerdto.getPassword(), "123"));
						customerdao.save(customerdto);
						// send otp to email
//						emailLogic.sendOtp(customerdto);
						map.put("fail", "Verify First"); // if Entered Wrong otp
						map.put("id", customerdto.getId());
						return "EnterOtp";
					}
				} else { // <--------------------------
					map.put("fail", "InValid Password");
					return "Login";
				}
			}

		}
		return null;

	}
}
