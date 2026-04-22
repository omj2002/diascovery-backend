package discovery.com.di.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long TIME_WINDOW_MS = 60000; // 1 minute

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().contains("/api/auth/login")) {
            String clientIp = getClientIP(request);
            LoginAttempt attempt = attemptsCache.computeIfAbsent(clientIp, k -> new LoginAttempt());

            long currentTime = System.currentTimeMillis();
            if (currentTime - attempt.lastAttemptTime > TIME_WINDOW_MS) {
                attempt.count = 0;
            }

            attempt.count++;
            attempt.lastAttemptTime = currentTime;

            if (attempt.count > MAX_ATTEMPTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many login attempts. Please try again later.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private static class LoginAttempt {
        int count = 0;
        long lastAttemptTime = System.currentTimeMillis();
    }
}
