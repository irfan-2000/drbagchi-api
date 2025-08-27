package com.drbagchisclasses.drbagchi_api.config;

import com.drbagchisclasses.drbagchi_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    public String UserId ;
    public String Email;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer "))
        {
            token = authHeader.substring(7);

             if (JwtUtil.validateToken(token))
             {
                String username = JwtUtil.extractUsername(token);
                this.UserId = JwtUtil.extractUserId(token);
                this.Email = JwtUtil.extractEmail(token);
                 UsernamePasswordAuthenticationToken auth =
                         new UsernamePasswordAuthenticationToken(username, null, List.of(() -> "ROLE_USER"));

                 SecurityContextHolder.getContext().setAuthentication(auth);
                 System.out.println("🔹 Authentication set: " + SecurityContextHolder.getContext().getAuthentication());

             }
        }

        // Continue filter chain - public endpoints will proceed without authentication
        filterChain.doFilter(request, response);
    }




}