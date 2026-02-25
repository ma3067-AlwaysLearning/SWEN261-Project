package com.example.cersystem.controllers;


import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

@Controller

public class AuthController {
    @GetMapping("/login")
    public String login(@RequestParam(required = false, value = "error") String error, @RequestParam(required = false) String logout, Model model, HttpServletRequest httpServletRequest) {
        if (error != null){
        model.addAttribute("error", "error");
        }

        if (logout != null){
            model.addAttribute("logout", "logout");
        }

        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute("_csrf");
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        return "login";
    }

}

