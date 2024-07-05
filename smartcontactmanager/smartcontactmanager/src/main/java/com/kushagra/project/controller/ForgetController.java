package com.kushagra.project.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kushagra.project.entities.User;
import com.kushagra.project.repo.UserRepository;
import com.kushagra.project.service.EmailService;

import jakarta.servlet.http.HttpSession;


@Controller
public class ForgetController {
	
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	Random random = new Random(1000);
	
	@GetMapping("/forget")
	public String openEmailForm() {

		return "user/openemailform";
	}
	
	
	@GetMapping("/sendotp")
	public String sendOtp(@RequestParam("email") String email, Model m, HttpSession session) {
		
		System.out.println(email);
		
		int otp = random.nextInt(9999999);
		
		System.out.println(otp);
		
		String message = "<h1> OTP = "+otp+" </h1>";
		String subject = "Testing";
	   //String to = "2019uce1134@gmail.com";
		String from = "kushagragupta76@gmail.com";
		
		boolean b = emailService.sendEmail(subject, message, email, from);
		
		System.out.println(b);
		
		if(b) {
			
			
			session.setAttribute("email", email);
			session.setAttribute("otp", otp );
			
			m.addAttribute("message", "OTP Sent successfully");
			
			return "user/verifyotp";
			
		}else {
			
			m.addAttribute("message", "Something wrong happen");
			return "user/openemailform";
		}

	}
	
	@PostMapping("/verifyotp")
	public String otpVerify(@RequestParam("otp") int otp, HttpSession session, Model m) {
		
		int systemgeneratedOtp = (Integer)session.getAttribute("otp");
		String email = (String)session.getAttribute("email");
		
		
		if(otp==systemgeneratedOtp) {
			User user = userRepository.findByEmail(email);
			
			if(user == null) {
				m.addAttribute("message", "User not found");
				return "user/openemailform";
			}else {
				m.addAttribute("message", "Change Password");
			    return "user/changepassword";
			}
			
		}else {
			
			m.addAttribute("message", "Wrong Otp");
			
			return "user/verifyotp";
		}
		
		
		
	}
	
	
	@PostMapping("/changepassword")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session, Model m) {
		
		
		try {
			
			String email = (String)session.getAttribute("email");
			System.out.println(email);
			User user = userRepository.findByEmail(email);
			user.setPassword(bCryptPasswordEncoder.encode (newPassword));
			userRepository.save(user);
			
			m.addAttribute("message", "password change successfully");
			
			return "login";
			
		} catch (Exception e) {
			
			e.printStackTrace();
			m.addAttribute("message", "password not change successfully");
			return "user/changepassword"; 
		}
		
		
		
		
		
		
		
	}
	
	

}
