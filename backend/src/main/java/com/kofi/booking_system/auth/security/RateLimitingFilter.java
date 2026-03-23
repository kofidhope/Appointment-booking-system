package com.kofi.booking_system.auth.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Stores buckets per IP + endpoint type
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    //  Bucket Creator
    private Bucket createBucket(int capacity, int refillTokens, Duration duration) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(refillTokens, duration));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    //  Extract Real Client IP
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            // Handle multiple IPs
            if (ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        }
        return request.getRemoteAddr();
    }

    //  Get USER ID from Spring Security
    private String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If user is logged in
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // Usually email or username
            return auth.getName();
        }
        return null; // Not logged in
    }

    // Skip Non-Important Endpoints
    private boolean shouldSkip(String path) {
        return path.startsWith("/actuator") ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/static") ||
                path.startsWith("/css") ||
                path.startsWith("/js") ||
                path.startsWith("/images");
    }

    //  Decide WHICH rate limit to apply
    private Bucket resolveBucket(String ip, String userId, String path, String method) {

        // Login (very strict)
        if (path.startsWith("/api/v1/auth/login")) {
            return buckets.computeIfAbsent(ip + ":login",
                    k -> createBucket(5, 5, Duration.ofMinutes(1)));
        }

        // Register (strict)
        if (path.startsWith("/api/v1/auth/register")) {
            return buckets.computeIfAbsent(ip + ":register",
                    k -> createBucket(3, 3, Duration.ofMinutes(1)));
        }

        // 👤 LOGGED-IN USERS → use USER ID
        if (userId != null) {
            // WRITE operations
            if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
                return buckets.computeIfAbsent(userId + ":write",
                        k -> createBucket(30, 30, Duration.ofMinutes(1)));
            }
            //  READ operations
            return buckets.computeIfAbsent(userId + ":read",
                    k -> createBucket(100, 100, Duration.ofMinutes(1)));
        }

        // GUEST USERS (not logged in) → fallback to IP
        return buckets.computeIfAbsent(ip + ":guest",
                k -> createBucket(50, 50, Duration.ofMinutes(1)));
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip some endpoints
        if (shouldSkip(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        //  Extract request info
        String method = request.getMethod();
        String ip = getClientIp(request);
        String userId = getUserId();

        //  Get  bucket
        Bucket bucket = resolveBucket(ip, userId, path, method);

        // Try consume token
        if (bucket.tryConsume(1)) {
            // send remaining tokens info
            response.setHeader("X-Rate-Limit-Remaining",
                    String.valueOf(bucket.getAvailableTokens()));
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\": 429, \"error\": \"Too Many Requests\", " +
                            "\"message\": \"Rate limit exceeded. Please try again later.\"}"
            );
            response.getWriter().flush();
        }
    }
}