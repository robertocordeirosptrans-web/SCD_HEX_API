package br.sptrans.scd.shared.audit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.sptrans.scd.audit.domain.AuditEventType;

/**
 * Marca um método como auditável.
 *
 * <p>O {@link AuditAspect} intercepta o retorno bem-sucedido e registra
 * o evento de auditoria via {@link br.sptrans.scd.audit.application.port.in.AuditUseCase},
 * usando o contexto corrente do {@link AuditContext}.</p>
 *
 * <p>Exemplo:</p>
 * <pre>{@code
 * @Auditable(action = AuditEventType.SESSION_REVOKED)
 * public void revokeSession(String sessionId, Long revokedBy, String reason) { ... }
 * }</pre>
 *
 * <p>Para capturar o ID do usuário alvo, use {@code targetIdParam}
 * com o nome exato do parâmetro do método:</p>
 * <pre>{@code
 * @Auditable(action = AuditEventType.SESSION_REVOKED, targetIdParam = "revokedBy")
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {

    /** Tipo do evento a ser registrado. */
    AuditEventType action();

    /**
     * Nome do parâmetro {@code Long} que representa o ID do usuário alvo.
     * Deixe em branco se não houver alvo.
     */
    String targetIdParam() default "";
}
