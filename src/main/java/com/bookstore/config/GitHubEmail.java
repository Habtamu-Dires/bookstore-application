package com.bookstore.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubEmail {
    private String email;
    private boolean primary;
    private boolean verified;
    private String visibility;
}
