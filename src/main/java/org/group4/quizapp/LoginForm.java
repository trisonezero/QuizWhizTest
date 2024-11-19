package org.group4.quizapp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class LoginForm {

    @NotEmpty(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
