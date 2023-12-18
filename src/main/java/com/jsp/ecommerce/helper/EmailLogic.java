package com.jsp.ecommerce.helper;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.jsp.ecommerce.dto.Customerdto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailLogic {

	@Autowired
	JavaMailSender mailSender;

	public void sendOtp(Customerdto customerdto) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setFrom("prajwaldurgoji16@gmail.com", "E-Commerce");
			helper.setTo(customerdto.getEmail());
			helper.setSubject("Verify OTP");
			helper.setText("<html><body><h1>Hello " + customerdto.getName() + "</h1><h2>Your Otp is: "
					+ customerdto.getOtp() + "</h2><h3>Thanks and Regards</h3></body></html>", true);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		mailSender.send(message);
	}
}
