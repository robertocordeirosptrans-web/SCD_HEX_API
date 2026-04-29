package br.sptrans.scd.audit.domain;

/**
 * Tipos de eventos de auditoria rastreados pelo sistema.
 *
 * Convenção:
 *  - LOGIN_*       → autenticação
 *  - SESSION_*     → ciclo de vida de sessões
 *  - LOGOUT        → encerramento voluntário
 *  - ACCESS_*      → autorização/segurança
 *  - PASSWORD_*    → gestão de senhas
 */
public enum AuditEventType {

    // ── Autenticação ──────────────────────────────────────────────────────────
    LOGIN_SUCCESS,
    LOGIN_FAILURE,

    // ── Sessões ───────────────────────────────────────────────────────────────
    SESSION_CREATED,
    SESSION_REVOKED,
    SESSION_EXPIRED,

    // ── Logout ────────────────────────────────────────────────────────────────
    LOGOUT,

    // ── Segurança / Autorização ───────────────────────────────────────────────
    ACCESS_DENIED,
    REQUEST_UNAUTHORIZED,

    // ── Senhas ────────────────────────────────────────────────────────────────
    PASSWORD_RESET_REQUESTED,
    PASSWORD_CHANGED,
    ADMIN_PASSWORD_RESET
}
