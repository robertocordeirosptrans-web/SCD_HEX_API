package br.sptrans.scd.shared.audit;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro HTTP que inicializa o {@link AuditContext} e o MDC (Mapped Diagnostic Context)
 * para cada request, permitindo rastreabilidade de logs e auditoria correlacionados.
 *
 * <p>Campos populados:</p>
 * <ul>
 *   <li>{@code traceId}  — UUID único por request</li>
 *   <li>{@code ip}       — IP de origem (considera X-Forwarded-For)</li>
 *   <li>{@code userId}   / {@code sessionId} — preenchidos posteriormente pelo JwtAuthFilter</li>
 * </ul>
 *
 * <p>Garante limpeza do MDC e do AuditContext ao final do request,
 * evitando vazamento de dados entre threads do pool.</p>
 */
@Component
@Order(1)
public class AuditContextFilter extends OncePerRequestFilter {

    private static final String MDC_TRACE_ID  = "traceId";
    private static final String MDC_IP        = "ip";
    private static final String MDC_USER_ID   = "userId";
    private static final String MDC_SESSION   = "sessionId";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String requestId  = UUID.randomUUID().toString();
        String ipAddress  = resolveClientIp(request);
        String userAgent  = request.getHeader("User-Agent");

        // Inicializa ThreadLocal
        AuditContext.init(requestId, ipAddress, userAgent);

        // Inicializa MDC (campos que já são conhecidos aqui)
        MDC.put(MDC_TRACE_ID, requestId);
        MDC.put(MDC_IP, ipAddress);
        MDC.put(MDC_USER_ID, "-");
        MDC.put(MDC_SESSION, "-");

        try {
            chain.doFilter(request, response);
        } finally {
            AuditContext.clear();
            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_IP);
            MDC.remove(MDC_USER_ID);
            MDC.remove(MDC_SESSION);
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // X-Forwarded-For pode conter múltiplos IPs: cliente, proxies...
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
