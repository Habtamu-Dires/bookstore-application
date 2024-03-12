package com.bookstore.user;

import com.bookstore.authority.Authority;
import com.bookstore.book.Book;
import com.bookstore.user.token.ConfirmationToken;
import com.bookstore.user.token.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ConfirmationTokenService tokenService;

    public String signUpUser(User user) {
        User existedUser = userRepository
                .findByEmail(user.getEmail())
                .orElse(null);

        if(existedUser != null){
            if(existedUser.isEnabled()){
                throw new IllegalStateException("Email already Taken");
            }
            //create token and send email again
            User savedUser = userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new IllegalStateException("User Not Found"));

            return generateToken(savedUser);
        }

        //save user
        User userTobeSaved = User.builder()
                .firstName(user.getFirstName())
                .password(passwordEncoder.encode(user.getPassword()))
                .email(user.getEmail())
                .authorities(List.of(new Authority(null, "USER")))
                .build();
        User savedUser = userRepository.save(userTobeSaved);

        //generate toke and save
        return generateToken(savedUser);
    }

    public String generateToken(User user){
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        //save token
        tokenService.saveToken(confirmationToken);

        return token;
    }

    public User addUser(User user){
        return userRepository.save(user);
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public void addBook(Integer userId, Book book){
        User user = userRepository.findById(userId).orElse(null);
        if(user != null){
            List<Book> bookList = user.getBookList();
            if(!bookList.contains(book)){
                bookList.add(book);
                user.setBookList(bookList);
            }
            userRepository.save(user);
        }
    }

    public List<Book> bookList(Integer id){
        return userRepository.findAllBooks(id);
    }

    public void removeBook(Integer id, Book book) {
        User user = userRepository.findById(id).orElse(null);
        if(user != null){
            List<Book> bookList = user.getBookList();
            bookList.remove(book);
            user.setBookList(bookList);

            userRepository.save(user);
        }
    }

    public void enableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Email not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }
}
