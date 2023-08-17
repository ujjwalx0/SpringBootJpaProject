package com.example.Test.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller; // Import the correct Controller annotation
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Test.Entity.User;
import com.example.Test.Exception.LoginFailureException;
import com.example.Test.Repository.UserRepository;
import com.example.Test.Service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller 
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        // Fetch the user from the database based on the username
        User storedUser = userRepository.findByUsername(user.getUsername());

        if (storedUser != null) {
            if (storedUser.getLoginFailureCount() < 5 && passwordEncoder.matches(user.getPassword(), storedUser.getPassword())) {
                // Reset failed login attempts on successful login
                storedUser.setLoginFailureCount(0);
                storedUser.setAccountLocked(false);
                userRepository.save(storedUser);
                // Redirect to the dashboard page after successful login
                return "redirect:/dashboard";
            } else {
                // Increment failed login attempts and check if account should be locked
                storedUser.incrementFailedLoginAttempts();
                if (storedUser.getLoginFailureCount() >= 5) {
                    storedUser.setAccountLocked(true);
                }
                userRepository.save(storedUser);

                // Set the error message
                redirectAttributes.addFlashAttribute("error", "Invalid username or password");
                return "redirect:/login"; // Redirect back to the login page with the error message
            }
        } else {
            // Handle the case when user is not found
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/login";
        }
    }



    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setRole("ROLE_USER"); // Set default role for new users
        userRepository.save(user);

        return "redirect:/login";
    }
    
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; 
    }
    
    @PostMapping("/reset-login-failure-count/{usernameOrEmail}")
    public ResponseEntity<String> resetLoginFailureCountByUsernameOrEmail(@PathVariable String usernameOrEmail) {
        try {
            userService.resetLoginFailureCountByUsernameOrEmail(usernameOrEmail);
            return ResponseEntity.ok("Login failure count reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resetting login failure count.");
        }
    }
}
