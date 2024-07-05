package com.kushagra.project.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kushagra.project.entities.Contact;
import com.kushagra.project.entities.User;
import com.kushagra.project.helper.Message;
import com.kushagra.project.repo.ContactRepository;
import com.kushagra.project.repo.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principle) {

		String name = principle.getName();
       
		User user = userRepository.findByEmail(name);
		user.setImageUrl("default.png");

		model.addAttribute("user", user);

	}

	@GetMapping("/loginverification")
	public String loginVerification() {

		return "user/dashboard";

	}

	@GetMapping("/addContact")
	public String openAddContactForm(Model model) {

		model.addAttribute("tittle", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "user/addcontact";
	}

	@PostMapping("/processContact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, Principal principal) throws IOException {

		try {

			if (file.isEmpty()) {

				System.out.println("file is empty");
				contact.setImage("images.png");

			} else {

				contact.setImage(file.getOriginalFilename());

				File file2 = new ClassPathResource("/static/img").getFile();
				System.out.println(file2);
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}

			String name = principal.getName();

			User user = userRepository.findByEmail(name);

			contact.setUser(user);

			user.getContacts().add(contact);

			//userRepository.save(user);

			contactRepository.save(contact);

			model.addAttribute("message", new Message("contact added successfully", "success"));

		} catch (Exception e) {

			System.out.println("Error" + e.getMessage());
			model.addAttribute("message", new Message("contact not added", "danger"));
		}

		return "user/addcontact";
	}

	@GetMapping("/showContact/{page}")
	public String showContact(@PathVariable("page") Integer page,  Model m, Principal principal) {

		String name = principal.getName();

		User user = userRepository.findByEmail(name);
		
		//Current page - page
		//Current per page - 5

		Pageable pageable = PageRequest.of(page, 3);

		
		  Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);
		 
		  
		  m.addAttribute("contacts", contacts);
		  m.addAttribute("currentpage", page );
		  m.addAttribute("totalpage", contacts.getTotalPages());
		  
		

		return "user/showContact";
	}
	
	@GetMapping("/{cId}/contact")
	public String showContactsDetails(@PathVariable("cId") Integer cId, Model m, Principal principal) {
		System.out.println(cId);
	    String name = principal.getName();
	    User user = userRepository.findByEmail(name);
	    
		Optional<Contact> contactDetails = contactRepository.findById(cId);
		Contact contact = contactDetails.get();
		
		if(user.getId()==contact.getUser().getId())
		m.addAttribute("contact", contact );
		
		return "user/contactdetails";
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model m, Principal principal) {
		
		String name = principal.getName();
		
		User user = userRepository.findByEmail(name);
		
		
		
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		if(user.getId()==contact.getUser().getId()) {
			
		user.getContacts().remove(contact);
		userRepository.save(user);
			
		}
		m.addAttribute("message" , new Message("succesfully added", "success"));
		return "redirect:/user/showContact/0";
		
	}
	
	@GetMapping("/update/{cId}")
	public String updateContactForm(@PathVariable("cId") Integer cId,  Model m) {
		
		Contact contact = contactRepository.findById(cId).get();
		
		m.addAttribute("contact", contact);
		
		return "user/updateform";
	}
	
	@PostMapping("/updateContact")
	public String updateContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, Principal principal) throws IOException {
		

		try {
			
		  
			Contact oldContactDetail = contactRepository.findById(contact.getcId()).get();
			

			if (!file.isEmpty()) {
				
				//deletephoto
				
				File deletefile = new ClassPathResource("/static/img").getFile();
				File file3 = new File(deletefile, oldContactDetail.getImage() );
                file3.delete();
				
				
				//upload new file
				File file2 = new ClassPathResource("/static/img").getFile();
				System.out.println(file2);
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				

			} else {

				contact.setImage(oldContactDetail.getImage());

		

			}

			String name = principal.getName();

			User user = userRepository.findByEmail(name);

			contact.setUser(user);

//			user.getContacts().add(contact);
//
//			userRepository.save(user);

			contactRepository.save(contact);

			model.addAttribute("message", new Message("contact updated successfully", "success"));

		} catch (Exception e) {

			System.out.println("Error" + e.getMessage());
			model.addAttribute("message", new Message("contact not updated", "danger"));
		}

		return "user/addcontact";
	}
	
	@GetMapping("/profile")
	public String userProfile(Model m) {
		
		
		m.addAttribute("tittle","Your Profile");
		return "user/profile";
		
	}
	
	
	@GetMapping("/setting")
	public String openSetting() {
		
		
		return "user/settings";
		
	}
	
	
	@PostMapping("/changepassword")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
			Principal principal, Model m) {
		
		
		
		User user = userRepository.findByEmail(principal.getName());
		
		if(bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(user);
			m.addAttribute("message", new Message("password change successfully", "success") );
			
			
		}else {
			
			m.addAttribute("message", new Message("please enter correct old password", "danger") );
			
		}
		
		
		
		return "user/dashboard";
	}
	
	
	
	
	
	

}
