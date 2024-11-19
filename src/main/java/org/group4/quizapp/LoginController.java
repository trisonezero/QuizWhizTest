package org.group4.quizapp;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
@RequestMapping("/login")
public class LoginController {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @GetMapping
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login-page"; // Make sure this matches your Thymeleaf template name
    }

    @PostMapping
    public String loginUser(@ModelAttribute("loginForm") @Valid LoginForm loginForm, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "login-page"; // Stay on the login page if there are validation errors
        }

        sanitizeInput(loginForm);

        // Find the user by email
        User user = findUserByEmail(loginForm.getEmail());

        // If user is null or password doesn't match, show error
        if (user == null || !passwordEncoder.matches(loginForm.getPassword(), user.getPassword())) {
            model.addAttribute("errorMessage", "Invalid email or password.");
            return "login-page"; // Return to the login page with an error
        }

        // Store user info in session
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("id", user.getId());

        return "redirect:/home"; // Redirect to home page on successful login
    }

    // Sanitize the input to prevent unwanted characters (basic example)
    private void sanitizeInput(LoginForm loginForm) {
        loginForm.setEmail(loginForm.getEmail().replaceAll("[^a-zA-Z0-9@._-]", ""));
    }

    // Fetch user details from the database by email
    private User findUserByEmail(String email) {
        try (Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)) {
            String query = "SELECT id, username, email, password FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        User user = new User();
                        user.setId(resultSet.getLong("id"));
                        user.setUsername(resultSet.getString("username"));
                        user.setEmail(resultSet.getString("email"));
                        user.setPassword(resultSet.getString("password"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user not found
    }
}
