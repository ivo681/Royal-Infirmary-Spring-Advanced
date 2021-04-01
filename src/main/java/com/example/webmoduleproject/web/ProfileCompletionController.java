package com.example.webmoduleproject.web;

import com.example.webmoduleproject.model.binding.CompleteProfileBindingModel;
import com.example.webmoduleproject.model.entities.enums.HealthCareJobsEnum;
import com.example.webmoduleproject.model.service.CompleteProfileServiceModel;
import com.example.webmoduleproject.model.view.GpViewModel;
import com.example.webmoduleproject.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProfileCompletionController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public ProfileCompletionController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/about-us")
    public String aboutUs() {
        return "about-us";
    }

    @GetMapping("/complete-profile")
    public String completeProfile(Principal principal,
                                  Model model) {
        String userEmail = principal.getName();
        if (this.userService.hasTelephone(userEmail)) {
            return "redirect:/home";
        }
        if (this.userService.hasMdRole(userEmail)) {
            model.addAttribute("hospitalId", this.userService.getHospitalId(userEmail));
        }
        if (!model.containsAttribute("completeProfileBindingModel")) {
            model.addAttribute("completeProfileBindingModel", new CompleteProfileBindingModel());
        }
        return "complete-profile";
    }

    @PatchMapping("/choose-gp/{id}")
    public String chooseGp(@PathVariable("id") String id, Principal principal) {
        String userEmail = principal.getName();
        this.userService.setGpById(userEmail, id);
        if (this.userService.hasMdRole(userEmail)) {
            return "redirect:/choose-job";
        }
        return "redirect:/home";
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @PatchMapping("/choose-job/{job}")
    public String chooseJob(@PathVariable("job") String job, Principal principal) {
        String userEmail = principal.getName();
        if (this.userService.hasJob(userEmail)) {
            return "redirect:/home";
        }
        this.userService.addMdJob(userEmail, job);
        return "redirect:/home";
    }

    @PatchMapping("/complete-profile")
    public String completeProfile(@Valid @ModelAttribute("completeProfileBindingModel") CompleteProfileBindingModel completeProfileBindingModel,
                                  BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal) {
        String userEmail = principal.getName();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("completeProfileBindingModel", completeProfileBindingModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.completeProfileBindingModel",
                    bindingResult);
            return "redirect:/complete-profile";
        }
        this.userService.completeProfile(this.modelMapper.map(completeProfileBindingModel, CompleteProfileServiceModel.class), userEmail);
        return "redirect:/choose-gp";
    }

    @GetMapping("/choose-gp")
    public String chooseGp(Principal principal, Model model) {
        String userEmail = principal.getName();
        if (!this.userService.hasTelephone(userEmail)) {
            return "redirect:/complete-profile";
        }
        if (!this.userService.hasGp(userEmail)) {
            List<GpViewModel> allGps = this.userService.getAllGps(userEmail);
            model.addAttribute("allGps", allGps);
            model.addAttribute("changing", false);
            return "choosegp";
        }
        return "redirect:/home";
    }

    @PreAuthorize("hasRole('ROLE_MD')")
    @GetMapping("/choose-job")
    public String chooseJob(Principal principal, Model model) {
        String userEmail = principal.getName();
        if (!this.userService.hasTelephone(userEmail)) {
            return "redirect:/complete-profile";
        }
        if (!this.userService.hasGp(userEmail)) {
            return "redirect:/choose-gp";
        }

        if (this.userService.hasMdRole(userEmail) && !this.userService.hasJob(userEmail)) {
            List<String> MdJobs = Arrays.stream(HealthCareJobsEnum.values()).
                    map(HealthCareJobsEnum::getName).collect(Collectors.toList());
            model.addAttribute("jobs", MdJobs);
            return "choose-job";
        }
        return "redirect:/home";

    }

}

