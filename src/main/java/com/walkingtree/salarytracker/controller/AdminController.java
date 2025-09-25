package com.walkingtree.salarytracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.walkingtree.salarytracker.auth.User;
import com.walkingtree.salarytracker.auth.UserServiceImpl;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private UserServiceImpl userService;

	// Excel upload endpoint
	@PostMapping("/uploadExcel")
	public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
		// TODO: Implement Excel parsing and user import logic
		// For now, just redirect back
		// You can add a flash attribute for success/error message if needed
		return "redirect:/admin/adminDashboard";
	}

	// Download sample Excel endpoint
	@GetMapping("/sample-excel")
	@ResponseBody
	public ResponseEntity<InputStreamResource> downloadSampleExcel() throws Exception {
		ClassPathResource sampleFile = new ClassPathResource("SampleExcel.xlsx");
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=SampleExcel.xlsx")
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(new InputStreamResource(sampleFile.getInputStream()));
	}

	@RequestMapping("/adminDashboard")
	public String adminHome(Model model, Authentication authentication) {

		if (authentication instanceof OAuth2AuthenticationToken) {

			OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
			OAuth2User oauth2User = oauthToken.getPrincipal();

			String email = oauth2User.getAttribute("email");
			User user = userService.findByEmail(email);

			List<User> users = userService.getAllUsers();
			model.addAttribute("users", users);
			model.addAttribute("name", user.getFirstName());
			model.addAttribute("isAuthenticated", authentication.isAuthenticated());
			System.out.println(model);
			return "admin/adminDashboard";
		}

		User user = userService.findByEmail(authentication.getName());

		System.out.println("details  from else");
		System.out.println(authentication.getName());
		List<User> users = userService.getAllUsers();

		model.addAttribute("users", users);
		model.addAttribute("name", user.getFirstName());
		model.addAttribute("isAuthenticated", authentication.isAuthenticated());

		// if (uploadMessage != null && !uploadMessage.isEmpty()) {
		// 	model.addAttribute("uploadMessage", uploadMessage);
		// }
		// if (uploadError != null && !uploadError.isEmpty()) {
		// 	model.addAttribute("uploadError", uploadError);
		// }
		System.out.println(model);
		return "admin/adminDashboard";
	}

	@GetMapping("/users/{id}/edit")
	public String editUserForm(@PathVariable("id") long id, Model model) {
		User user = userService.findById(id).get();
		model.addAttribute("user", user);
		return "admin/editUsers"; // A new Thymeleaf template for editing users
	}

	@PostMapping("/users/{id}/edit")
	public String updateUser(@PathVariable("id") long id, @ModelAttribute("user") User user) {

		userService.updateUser(id, user);

		return "redirect:/admin/adminDashboard"; // Redirect back to the user management page after update
	}

	@PostMapping("/users/{id}/delete")
	public String deleteUser(@PathVariable("id") long id) {

		System.out.println("from the delete " + id);
		userService.deleteUser(id);
		return "redirect:/admin/adminDashboard"; // Redirect back to the user management page after deletion
	}

}
