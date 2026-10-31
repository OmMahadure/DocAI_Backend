package com.main.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.main.service.JWTService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private final JWTService jwtService;
	
	public JwtAuthenticationFilter(JWTService jwtService) {
		this.jwtService=jwtService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException 
            {

                String header = request.getHeader("Authorization");

                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);

                    try {
                        String email = jwtService.extractEmail(token);

                        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            email,
                                            null,
                                            Collections.emptyList()
                                    );

                            SecurityContextHolder.getContext()
                                    .setAuthentication(authentication);
                        }

                    } catch (JwtException | IllegalArgumentException e) {
                        SecurityContextHolder.clearContext();
                    }
                }

                filterChain.doFilter(request, response);
            }
			
}
