package com.bookstore.user.registration;

public record RegistrationRequest(String firstName,
                                  String email,
                                  String password) {
}
