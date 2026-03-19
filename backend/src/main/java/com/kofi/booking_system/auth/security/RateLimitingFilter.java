package com.kofi.booking_system.auth.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> bucket = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        Bandwidth limit =  Bandwidth
                .classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket getBucket(String ip){
        return bucket.computeIfAbsent(ip, k -> createBucket());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Only rate limit auth endpoints
        String path = request.getRequestURI();
        if (!path.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        Bucket clientBucket = getBucket(ip);

        if (clientBucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\": 429, \"error\": \"Too Many Requests\", " +
                            "\"message\": \"Too many requests. Please try again later.\"}"
            );
            response.getWriter().flush();
        }

    }
}
