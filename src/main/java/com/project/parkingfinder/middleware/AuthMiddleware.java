package com.project.parkingfinder.middleware;

import org.springframework.stereotype.Component;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthMiddleware implements Filter {

    private static final String AUTH_HEADER = "Authorization"; // Tên header cho token

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ServletException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Lấy token từ header

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Dọn dẹp tài nguyên nếu cần
    }
}
