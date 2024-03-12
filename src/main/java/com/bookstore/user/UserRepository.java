package com.bookstore.user;

import com.bookstore.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email =:email")
    Optional<User> findByEmail(String email);

    @Query("SELECT u.bookList FROM User u WHERE u.id =:id")
    List<Book> findAllBooks(Integer id);
}
