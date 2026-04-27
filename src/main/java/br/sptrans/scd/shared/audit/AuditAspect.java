package br.sptrans.scd.shared.audit;

import java.lang.reflect.Parameter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.audit.application.port.in.AuditUseCase;
import br.sptrans.scd.audit.domain.AuditEvent;
import lombok.RequiredArgsConstructor;

/**
 * Aspect AOP que intercepta métodos anotados com {@link Auditable}
 * e registra o evento via {@link AuditUseCase}.
 *
 * <p>Executa {@code @AfterReturning} — apenas em retornos bem-sucedidos.
 * Falhas devem ser auditadas explicitamente no código de tratamento de exceções.</p>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final AuditUseCase auditUseCase;

    @AfterReturning("@annotation(auditable)")
    public void onSuccess(JoinPoint joinPoint, Auditable auditable) {
        try {
            AuditContext.Data ctx = AuditContext.get();
            Long targetId = resolveTargetId(joinPoint, auditable.targetIdParam());

            auditUseCase.audit(new AuditEvent(
                    auditable.action(),
                    ctx.userId,
                    targetId,
                    ctx.sessionId,
                    ctx.ipAddress,
                    ctx.userAgent,
                    null
            ));
        } catch (Exception ex) {
            // AOP nunca deve quebrar o fluxo principal
            log.warn("[AUDIT] AuditAspect falhou para action={}: {}",
                    auditable.action(), ex.getMessage());
        }
    }

    /**
     * Resolve o valor {@code Long} de um parâmetro do método pelo nome.
     * Retorna {@code null} se o nome estiver vazio ou o parâmetro não for encontrado.
     */
    private Long resolveTargetId(JoinPoint jp, String paramName) {
        if (paramName == null || paramName.isBlank()) {
            return null;
        }
        Object[] args = jp.getArgs();
        Parameter[] params = ((org.aspectj.lang.reflect.MethodSignature)
                jp.getSignature()).getMethod().getParameters();

        for (int i = 0; i < params.length; i++) {
            if (paramName.equals(params[i].getName()) && args[i] instanceof Long l) {
                return l;
            }
        }
        return null;
    }
}
