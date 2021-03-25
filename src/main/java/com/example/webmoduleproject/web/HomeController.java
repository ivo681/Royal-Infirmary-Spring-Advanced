package com.example.webmoduleproject.web;

import com.example.webmoduleproject.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/home")
    public String home(Principal principal){
        String userEmail = principal.getName();
        if (!this.userService.hasTelephone(userEmail)){
            return "redirect:/complete-profile";
        }
        if (!this.userService.hasGp(userEmail)){
            return "redirect:/choose-gp";
        }

        if (this.userService.hasMdRole(userEmail) && !this.userService.hasJob(userEmail)){
            return "redirect:/choose-job";
        }
        return "home";
    }


}
