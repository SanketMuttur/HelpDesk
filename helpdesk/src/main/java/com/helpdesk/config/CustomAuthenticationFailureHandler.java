package com.helpdesk.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage = "Invalid username or password";
        
        if (exception instanceof LockedException) {
            errorMessage = exception.getMessage();
        }
        
        // URL encode the error message to handle special characters
        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
        
        setDefaultFailureUrl("/login?error=" + errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}