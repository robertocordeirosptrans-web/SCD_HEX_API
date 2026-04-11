package br.sptrans.scd.audit.application.port.in;

import br.sptrans.scd.audit.domain.AuditEvent;

/**
 * Input Port — Auditoria.
 *
 * Contrato para registrar um evento de auditoria:
 * persiste no banco {@code AUDIT_LOG} e emite log estruturado {@code [AUDIT]}.
 * Implementado por {@link br.sptrans.scd.audit.application.service.AuditService}.
 *
 * <p><strong>Garantia de fallback:</strong> falhas de persistência não propagam
 * exceções para o fluxo de chamada.</p>
 */
public interface AuditUseCase {

    /**
     * Registra o evento de auditoria de forma assíncrona.
     *
     * @param event dados completos do evento
     */
    void audit(AuditEvent event);
}
