package com.project.parkingfinder.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class APIKeyMiddleware extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY"; // Name of the header for the API key
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException  {
        // String requestUri = request.getRequestURI();
        // System.out.println("Request URI: " + requestUri);
        // if (!requestUri.startsWith("/api/reservations")) {
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        // List<ApiKey> apiKeys = ApiKeyLoader.loadApiKeys("api_keys.json");
        // String apiKey = request.getHeader(API_KEY_HEADER);
        // boolean isValidApiKey = apiKey != null && apiKeys.stream().anyMatch(key -> key.getApiKey().equals(apiKey));
        // System.out.println(">>> Received API Key: " + apiKey + ", Validation Result: " + isValidApiKey);
        // if (!isValidApiKey) {
        //     throw new ServletException("Invalid API Key");
        // }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Clean up resources if necessary
    }
}   
