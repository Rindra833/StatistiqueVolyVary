package com.Hasner.VolyVary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Redirige vers /WEB-INF/jsp/login.jsp
    }
}
