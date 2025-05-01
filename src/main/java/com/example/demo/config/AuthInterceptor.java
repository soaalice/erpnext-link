package com.example.demo.config;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.*;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("sid") == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        return true;
    }
}
