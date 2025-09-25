package com.walkingtree.salarytracker.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		var authourities=authentication.getAuthorities();
		var roles=authourities.stream().map(role->role.getAuthority()).findFirst();
		
		if(roles.orElse("").equalsIgnoreCase("ADMIN")) {
			response.sendRedirect("/admin/adminDashboard");
		}else if(roles.orElse("").equalsIgnoreCase("USER")){
			response.sendRedirect("/user/dashboard");
		}
	}

}
