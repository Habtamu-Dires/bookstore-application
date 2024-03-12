package com.bookstore.user.email;

public interface EmailSender {
    void send(String to, String email);
}
