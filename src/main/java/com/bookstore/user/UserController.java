package com.bookstore.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model, HttpServletRequest request){
        //create
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if(session != null){
            Exception exception = (Exception) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if(exception != null){
                errorMessage = exception.getMessage();
            }
        }
        model.addAttribute("message", errorMessage);
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("registerError", false);
        return "register";
    }
}
