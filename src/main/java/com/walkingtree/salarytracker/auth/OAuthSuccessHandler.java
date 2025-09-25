package com.walkingtree.salarytracker.auth;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> authorities = new ArrayList(authentication.getAuthorities());

        // Add the user's role from the database to the authorities
        authorities.add(new SimpleGrantedAuthority(user.getRole().toUpperCase())); // eg- ADMIN
        System.out.println(user.getRole());
        
        // Create a new Authentication token with the merged authorities
        Authentication newAuth = new OAuth2AuthenticationToken(
                oauthUser, 
                authorities, 
                oauthUser.getName()
        );

        // Setting the updated authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        String redirectUrl = authorities.stream()
                                        .anyMatch(auth -> auth.getAuthority().equals("ADMIN"))
                                        ? "admin/adminDashboard" : "user/dashboard";
        
        // Redirect the user
        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}