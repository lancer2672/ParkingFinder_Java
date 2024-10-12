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
public class APIKeyMiddleware implements Filter {

    private static final String API_KEY_HEADER = "X-API-KEY"; // Tên header cho API key
    private static final String VALID_API_KEY = "your_valid_api_key"; // Đặt API key hợp lệ

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo nếu cần
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String apiKey = httpRequest.getHeader(API_KEY_HEADER);

        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            throw new ServletException("Invalid API Key");
        }

        // Tiếp tục chuỗi filter
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Dọn dẹp tài nguyên nếu cần
    }
}
