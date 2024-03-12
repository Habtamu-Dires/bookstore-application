package com.bookstore.user.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public String register(@ModelAttribute RegistrationRequest registrationRequest,
                           Model model){
        try{
           registrationService.register(registrationRequest);
        } catch (Exception e){
            System.out.println(e.getMessage());
            model.addAttribute("registerError", true);
            model.addAttribute("message", e.getMessage());
            return "register";
        }


        return "redirect:/login";
    }

    @ResponseBody
    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }
}
