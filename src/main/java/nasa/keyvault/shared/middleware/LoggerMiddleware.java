package nasa.keyvault.shared.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nasa.keyvault.shared.logging.LoggingConstants;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

@Component
public class LoggerMiddleware extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        try {
            MDC.put(LoggingConstants.HttpMethodKey, request.getMethod());
            MDC.put(LoggingConstants.HostKey, InetAddress.getLocalHost().getHostName());
            MDC.put(LoggingConstants.TraceIdKey, UUID.randomUUID().toString().replace('-', ' '));

            filterChain.doFilter(request, response);

            MDC.put(LoggingConstants.SuccessKey, String.valueOf(response.getStatus() < 400));
        } finally {
            MDC.clear();
        }
    }
}
