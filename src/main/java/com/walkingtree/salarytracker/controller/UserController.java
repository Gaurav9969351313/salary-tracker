package com.walkingtree.salarytracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.walkingtree.salarytracker.auth.User;
import com.walkingtree.salarytracker.auth.UserServiceImpl;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserServiceImpl userService;
	
	
	@RequestMapping("/dashboard")
	public String dashboard(Authentication authentication, Model model) {
		//for OAuth Login 
		if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauthToken.getPrincipal();
            String email = oauth2User.getAttribute("email");
            User user= userService.findByEmail(email);
             
            System.out.println();
            
            model.addAttribute("isAuthenticated", authentication.isAuthenticated());
           
            model.addAttribute("user", user);
            model.addAttribute("name", user.getFirstName());
            
         
            return "/user/dashboard";
        }
		//for spring login
		model.addAttribute("isAuthenticated", authentication.isAuthenticated());
        model.addAttribute("user", userService.findByEmail(authentication.getName()));
		return "/user/dashboard";
	}
	
	 @GetMapping("/users/{id}/edit")
	    public String editUserForm(@PathVariable("id") long id, Model model) {
	        User user = userService.findById(id).get();
	        model.addAttribute("user", user);    
	        return "user/editUsers";  // A new Thymeleaf template for editing users
	    }

	    @PostMapping("/users/{id}/edit")
	    public String updateUser(@PathVariable("id") long id, @ModelAttribute("user") User user) {

	        userService.updateUser(id, user);
	        
	        return "redirect:/user/dashboard";  // Redirect back to the user management page after update
	    }
	
	
}
