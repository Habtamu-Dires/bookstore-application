package com.bookstore.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

//    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
//    private final CustomOidcUserService customOidcUserService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/registration/**").permitAll()
                .requestMatchers("/edit-book/**").hasAuthority("ADMIN")
                .requestMatchers("/delete-book/**").hasAuthority("ADMIN")
                .requestMatchers("/save").hasAuthority("ADMIN")
                .anyRequest().authenticated());

        http.formLogin(login -> login
                .loginPage("/login").permitAll()
                .failureUrl("/login-error")
                .defaultSuccessUrl("/home", true)
        );

        http.oauth2Login(oauth2Login -> oauth2Login
                .loginPage("/login").permitAll()
                .failureUrl("/login-error")
                .defaultSuccessUrl("/home", true)
                .userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint
//                        .oidcUserService(customOidcUserService)
                        .userService(customOAuth2UserService)
                )

        );

        http.logout(logout ->logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true));

        return http.build();
    }


}


