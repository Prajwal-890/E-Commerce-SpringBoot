package com.jsp.ecommerce.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//@Data          //generates getters and setters

@Component
@Entity
public class Customerdto {

	@Id
	@GeneratedValue(generator = "slno")
	@SequenceGenerator(initialValue = 101, allocationSize = 1, sequenceName = "slno", name = "slno")
	private int id;

	@NotEmpty(message = "*This is Mandatory")
	@Size(max = 10, min = 3, message = "*Enter between 3 to 10")
	private String name;

	@DecimalMin(value = "6000000000", message = "*Enter Proper Mobile Number")
	@DecimalMax(value = "9999999999", message = "*Enter Proper Mobile Number")
	private long mobile;

	@NotEmpty(message = "*This is Mandatory")
	@Email(message = "*Enter Proper Format")
	private String email;

	@Past(message = "*Enter Proper Date")
	@NotNull(message = "Select One Date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;

	@NotEmpty(message = "*This is Mandatory")
	@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "*Should Match Pattern")
	private String password;
	
	private int otp;
	boolean verifed;
	
	@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	ShoppingCart cart;
	

	public ShoppingCart getCart() {
		return cart;
	}

	public void setCart(ShoppingCart cart) {
		this.cart = cart;
	}

	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public boolean isVerifed() {
		return verifed;
	}

	public void setVerifed(boolean verifed) {
		this.verifed = verifed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMobile() {
		return mobile;
	}

	public void setMobile(long mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
