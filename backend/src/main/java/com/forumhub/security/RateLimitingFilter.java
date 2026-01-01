package com.forumhub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    
    @org.springframework.beans.factory.annotation.Value("${api.security.ratelimit.max-requests:100}")
    private int maxRequestsPerMinute;
    
    private static final String REDIS_PREFIX = "ratelimit:ip:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String key = REDIS_PREFIX + request.getRemoteAddr();
        
        try {
            // Incrementar o contador no Redis de forma atômica
            Long currentRequests = redisTemplate.opsForValue().increment(key);

            if (currentRequests != null && currentRequests == 1) {
                // Se for a primeira requisição, define o tempo de expiração para 1 minuto
                redisTemplate.expire(key, 60, TimeUnit.SECONDS);
            }

            if (currentRequests != null && currentRequests > maxRequestsPerMinute) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{ \"error\": \"Too many requests\", \"message\": \"Rate limit exceeded. Please try again in a minute.\" }");
                return;
            }
        } catch (Exception e) {
            // Em caso de falha no Redis (ex: testes sem Redis real), permitimos a requisição passar
            // Em produção o container backend depende do redis saudável.
            logger.warn("Redis connection failed for rate limiting: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}