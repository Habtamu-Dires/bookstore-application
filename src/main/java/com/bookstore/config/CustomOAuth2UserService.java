package com.bookstore.config;

import com.bookstore.authority.Authority;
import com.bookstore.security_user.SecurityUser;
import com.bookstore.user.User;
import com.bookstore.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

   private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email=null, name = null;
        System.out.println(attributes);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("The Registration Id " + registrationId);
        if("github".equals(registrationId)){
            System.out.println("IT is GITHUB");
            String login = (String) attributes.get("login");
            name = (String) attributes.get("name");
            //webClient to get email form github
            String url = "https://api.github.com/user/emails";
            //get token
            String accessToken = userRequest.getAccessToken().getTokenValue();
            WebClient webClient = WebClient.create();
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.AUTHORIZATION, "token" + accessToken);

            List<GitHubEmail> gitHubEmails = webClient.get()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<GitHubEmail>>() {
                    })
                    .block();
          try{
              if(gitHubEmails != null){
                  String primaryEmail = gitHubEmails.stream()
                          .filter(e -> e.isPrimary())
                          .findFirst()
                          .map(GitHubEmail::getEmail)
                          .orElseThrow(() -> new RuntimeException("NO Primary Email"));
                  email = primaryEmail;
              }
          } catch (Exception e){
              email = login;
          }


        } else if("GOOGLE".equalsIgnoreCase(registrationId)){
            System.out.println("It is google");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");

        }

        System.out.println("Teh name " + name);
        System.out.println("Teh eamil " + email);

        if(email != null && name != null){
            User user = saveUser(email, name);
            return updateOAuth2UserAttributes(oAuth2User, user, email);
        } else {
            throw new RuntimeException("Login Failed ");
        }

    }

    private OAuth2User updateOAuth2UserAttributes(OAuth2User oAuth2User,
                                                  User user,
                                                  String email){
        Map<String, Object> updatedAttributes = new HashMap<>(oAuth2User.getAttributes());

        updatedAttributes.put("id", user.getId());
        updatedAttributes.put("authority", user.getAuthorities().stream()
                .map(Authority::getName).toList());
        updatedAttributes.put("email", email);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                updatedAttributes,
                "email");
    }

    private User saveUser(String email, String name){
        User user = userService.findUserByEmail(email);
        if(user == null){ // lets save the user
            String randPassword = UUID.randomUUID().toString();
            User savedUser = User.builder()
                    .firstName(name)
                    .email(email)
                    .password(randPassword)
                    .authorities(List.of(new Authority(null, "USER")))
                    .enabled(true)
                    .build();
            //authenticate
            customAuthentication(savedUser);

            return userService.addUser(savedUser);
        }
        // update name in case it is changed
        user.setFirstName(name);
        customAuthentication(user);

        return userService.addUser(user);
    }

    private void customAuthentication(User user){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new SecurityUser(user),
                null,
                user.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                        .toList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
