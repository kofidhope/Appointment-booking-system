package com.kofi.booking_system.security;

import com.kofi.booking_system.service.CustomUserDetailsService;
import com.kofi.booking_system.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().startsWith("/api/v1/auth")){
            filterChain.doFilter(request,response);
            return;
        }

        //Get the Authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        //Check if the header is present and starts with "Bearer "
        if (authorizationHeader == null || authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // continue if  no token
            return;
        }

        // extract token
        jwt = authorizationHeader.substring(7); //remove bearer
        email=jwtService.extractSubject(jwt); // extract email form token

        //Check if SecurityContext is empty and token is valid
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserService.loadUserByUsername(email);
            if (jwtService.validateToken(jwt)) {
                //  Create authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetails(request));

                //  Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
