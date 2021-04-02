package com.example.webmoduleproject.web;

import com.example.webmoduleproject.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebInputException;

import java.rmi.ServerError;
import java.security.Principal;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index";
        }
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Principal principal) throws ServerError {
        String userEmail = principal.getName();
        if (!this.userService.hasTelephone(userEmail)) {
            return "redirect:/complete-profile";
        }
        if (!this.userService.hasGp(userEmail)) {
            return "redirect:/choose-gp";
        }

        if (this.userService.hasMdRole(userEmail) && !this.userService.hasJob(userEmail)) {
            return "redirect:/choose-job";
        }
        return "home";
    }


}
