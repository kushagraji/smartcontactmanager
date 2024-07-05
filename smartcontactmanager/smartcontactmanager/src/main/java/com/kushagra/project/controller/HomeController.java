package com.kushagra.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kushagra.project.entities.User;
import com.kushagra.project.repo.UserRepository;

import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	
	
	@GetMapping("/")
	public String home(ModelMap model) {
		
		model.addAttribute("tittle","testing");
		
		return "home";
	}
	
	@GetMapping("/about")
	public String about(ModelMap model) {
		
		model.addAttribute("tittle","testing");
		
		return "about";
	}
	
	@GetMapping("/signup")
	public String signUp(ModelMap model) {
		
		model.addAttribute("user", new User());
		
		return "signup";
	}
	
	@PostMapping("/doRegistered")
	public String registerUser(@Valid @ModelAttribute("user") User user , BindingResult result1 , @RequestParam(value = "myCheckbox" , defaultValue = "false") boolean agreement, ModelMap model ) {
		
		try {
			
			if(!agreement) {
				throw new Exception("You have not check terms and condition");
			}
			
			if(result1.hasErrors()) {
				model.addAttribute("message", "validation fail");
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println(agreement);
			User result = userRepository.save(user);
			System.out.println(result);
			model.addAttribute("user", new User() );
			model.addAttribute("message","registered successfully");
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "something went wrong" );
			
			return "signup"; 
		}
		
		
	}
	
	
	@GetMapping("/login")
	public String showLoginPage(ModelMap model) {
		
		return "login";
	}
	

}
