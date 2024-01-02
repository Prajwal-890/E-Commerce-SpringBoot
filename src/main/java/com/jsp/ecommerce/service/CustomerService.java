package com.jsp.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.jsp.ecommerce.dao.Customerdao;
import com.jsp.ecommerce.dao.Productdao;
import com.jsp.ecommerce.dto.Customerdto;
import com.jsp.ecommerce.dto.Item;
import com.jsp.ecommerce.dto.PaymentDetails;
import com.jsp.ecommerce.dto.Productdto;
import com.jsp.ecommerce.dto.ShoppingCart;
import com.jsp.ecommerce.helper.AES;
import com.jsp.ecommerce.helper.EmailLogic;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerService {

	@Autowired
	Customerdao customerdao;

	@Autowired
	Productdao productdao;

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

	public String fetchProducts(ModelMap map, Customerdto customerdto) {
		List<Productdto> productdtos = productdao.fetchDisplayProducts();
		if (productdtos.isEmpty()) {
			map.put("fail", "No Products Present");
			return "CustomerHome";
		} else {
			if (customerdto.getCart() == null)
				map.put("items", null);
			else {
				map.put("items", customerdto.getCart().getItems());
			}
			map.put("products", productdtos);
			return "CustomerViewProduct";
		}
	}

	public String addToCart(Customerdto customerdto, int id, HttpSession session, ModelMap map) {
		Productdto productdto = productdao.findById(id);

		ShoppingCart cart = customerdto.getCart();
		if (cart == null) {
			cart = new ShoppingCart();
		}

		List<Item> items = cart.getItems();
		if (items == null) {
			items = new ArrayList<Item>();
		}

		if (productdto.getStock() > 0) {
			boolean flag = true;

			for (Item item : items) {
				// if item already exists in cart
				if (item.getName().equals(productdto.getName())) {
					flag = false;
					item.setQuantity(item.getQuantity() + 1);
					item.setPrice(item.getPrice() + productdto.getPrice());
					break;
				}
			}
			if (flag) {
				// if items is New in cart
				Item item = new Item();
				item.setCategory(productdto.getCategory());
				item.setName(productdto.getName());
				item.setPicture(productdto.getPicture());
				item.setPrice(productdto.getPrice());
				item.setQuantity(1);
				items.add(item);

			}
			// after adding the items in the cart, update the existing cart
			cart.setItems(items); // Sets the updated list of items in the cart.
			cart.setTotalAmount(cart.getItems().stream().mapToDouble(x -> x.getPrice()).sum());
			customerdto.setCart(cart); // Sets the updated shopping cart for the customer.
			customerdao.save(customerdto);// Saves the updated customer information to the database.
			//// updating stock
			productdto.setStock(productdto.getStock() - 1);
			productdao.save(productdto);

			session.setAttribute("customer", customerdto);
			map.put("pass", "Product Added to Cart");
			return fetchProducts(map,customerdto);

		} else {
			map.put("fail", "Out of Stocks");
			return fetchProducts(map,customerdto);
		}
	}

	public String viewCart(Customerdto customerdto, ModelMap map) {
		ShoppingCart cart = customerdto.getCart();
		if (cart == null || cart.getItems().isEmpty()) {
			map.put("fail", "No items in Cart");
			return "CustomerHome";
		} else {
			map.put("cart", cart);
			return "ViewCart";
		}
	}

	public String removeFromCart(Customerdto customerdto, int id, ModelMap map, HttpSession session) {
		Productdto productdto = productdao.findById(id);

		ShoppingCart cart = customerdto.getCart();
		if (cart == null) {
			map.put("fail", "Item not in cart");
			return fetchProducts(map,customerdto); // customerHome page
		} else {
			List<Item> items = cart.getItems();
			if (items == null || items.isEmpty()) {
				map.put("fail", "items not in cart");
				return fetchProducts(map,customerdto);
			} else {
				Item item = null;   //creating new variable item assigning null
				for (Item item2 : items) {
					if (item2.getName().equals(productdto.getName())) {
						item = item2;
						break;
					}
				}
				if (item == null) {
					map.put("fail", "Item not in Cart");
					return fetchProducts(map,customerdto);
				} else {
					if (item.getQuantity() > 1) { // Checks if there is more than one of the found item in the basket.
						item.setQuantity(item.getQuantity() - 1); // If there's more than one, decreases the quantity of
																	// that item in your basket by 1.
						item.setPrice(item.getPrice() - productdto.getPrice());// Also decreases the total price of that
																				// item in my basket based on the price
																				// of the product.
					} else { // If there's only one of the item in your basket, proceed to the next block.
						items.remove(item); // Removes the item from your basket because you're taking the last one.
					}
				}
				cart.setItems(items); // Updates the contents of your basket (the items in the shopping cart) based on
										// what I did in the previous steps.
				cart.setTotalAmount(cart.getItems().stream().mapToDouble(x -> x.getPrice()).sum()); // Recalculates the
																									// total amount
																									// (price) of all
																									// items in the
																									// shopping cart
				
				customerdto.setCart(cart); // Updates the customer's information to reflect the changes made to the
											// shopping cart.
				customerdao.save(customerdto);
				// updating stock
				productdto.setStock(productdto.getStock() + 1);
				productdao.save(productdto);

				if (item != null && item.getQuantity() == 1)
					productdao.deleteItem(item);
				session.setAttribute("customer", customerdto);
				map.put("pass", "Product Removed from Cart");
				return fetchProducts(map,customerdto);
			}
		}

	}

	public String createOrder(Customerdto customerdto, ModelMap map) throws RazorpayException {
		RazorpayClient client = new RazorpayClient(null, null);
		
		JSONObject object = new JSONObject();
		object.put("amount", customerdto.getCart().getTotalAmount()*100);
		object.put("currency", "INR");
		
		Order order = client.orders.create(object);
		
		PaymentDetails details = new PaymentDetails();
		details.setAmount(customerdto.getCart().getTotalAmount());
		details.setCurrency(order.get("currency"));
		details.setDescription("Shopping Cart Payment for the products");
		details.setImage("https://www.shutterstock.com/image-vector/mobile-application-shopping-online-on-260nw-1379237159.jpg");
		details.setKey("rzp_test_XdF3iSaishFpgwckm1");
		details.setName("Ecommerce Shopping");
		details.setOrder_id(order.get("id"));
		details.setStatus("created");
		
		System.out.println(details);
		
		map.put("details", details);
		map.put("customer", customerdto);
		
		return "PaymentPage";
	}

}
