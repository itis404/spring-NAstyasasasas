package com.example.tripsplit.controller;

import com.example.tripsplit.dto.RegisterRequest;
import com.example.tripsplit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class AuthController {
    private final UserService userService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "auth/register";

        try {
            userService.register(registerRequest);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("email", "error.email", exception.getMessage());
            return "auth/register";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
}
