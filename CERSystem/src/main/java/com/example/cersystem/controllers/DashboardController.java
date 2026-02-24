package com.example.cersystem.controllers;

import com.example.cersystem.repositories.UserRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;


@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/dashboard")
    public String getDashboard(Model model, Principal principal, HttpServletRequest httpServletRequest){
        userRepository.findByEmail(principal.getName()).ifPresent(user -> {
            model.addAttribute("name",          user.getName());
            model.addAttribute("email",         user.getEmail());
            model.addAttribute("university_id", user.getUniversity_id());
            model.addAttribute("role",          user.getRole());
        });

        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute("_csrf");
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        return "dashboard";
    }
}
