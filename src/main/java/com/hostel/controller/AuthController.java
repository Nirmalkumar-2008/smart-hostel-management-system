package com.hostel.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                } else if (authority.getAuthority().equals("ROLE_STUDENT")) {
                    return "redirect:/student/dashboard";
                }
            }
        }
        return "login";
    }

    @GetMapping("/")
    public String rootRedirect(Authentication authentication) {
        return loginPage(authentication);
    }
}
