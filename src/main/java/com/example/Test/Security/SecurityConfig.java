package com.example.Test.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.example.Test.Entity.User;
import com.example.Test.Repository.UserRepository;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
    
        .headers()
            .frameOptions().sameOrigin() // Set X-Frame-Options to SAMEORIGIN
            .and()
        .authorizeRequests()
            .antMatchers("/register").permitAll()
            .antMatchers("/login").permitAll()
            .antMatchers("/").permitAll()
            .antMatchers("/**/*.css", "/**/*.js").permitAll()
            .antMatchers("/dashboard").authenticated()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard")
            .failureHandler((request, response, exception) -> {
                String errorMessage = "Invalid login credentials.";
                String loginField = request.getParameter("username"); // This can be either username or email
                User user = userRepository.findByUsernameOrEmail(loginField, loginField);

                if (user != null && user.getLoginFailureCount() >= 5) {
                    errorMessage = "Your account is locked due to multiple login failures.";
                }
                
                request.getSession().setAttribute("error", errorMessage);
                response.sendRedirect("/login?error=true");
            })
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler(logoutSuccessHandler())
            .permitAll()
            .and()
            .csrf().disable();
    }
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            clearCookies(request, response); // Clear cookies
            response.sendRedirect("/login"); // Redirect to the login page
        };
    }

    private void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String loginField = authentication.getName();
                String password = authentication.getCredentials().toString();

                User user = userRepository.findByUsernameOrEmail(loginField, loginField);

                if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                    if (user.getLoginFailureCount() >= 5) {
                        throw new LockedException("Account locked due to multiple login failures");
                    }

                    // Reset login failure count on successful login
                    user.setLoginFailureCount(0);
                    userRepository.save(user);

                    // Proceed with successful authentication
                    return new UsernamePasswordAuthenticationToken(loginField, password, new ArrayList<>());
                } else {
                    // Increment login failure count and check if account should be locked
                    if (user != null) {
                        user.incrementFailedLoginAttempts();
                        if (user.getLoginFailureCount() >= 5) {
                            user.setAccountLocked(true);
                        }
                        userRepository.save(user);
                    }

                    throw new BadCredentialsException("Invalid login credentials");
                }
            }

            @Override
            public boolean supports(Class<?> authenticationType) {
                return authenticationType.equals(UsernamePasswordAuthenticationToken.class);
            }
        });
    

    }
}
