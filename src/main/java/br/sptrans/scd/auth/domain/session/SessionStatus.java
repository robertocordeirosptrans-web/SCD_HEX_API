package br.sptrans.scd.auth.domain.session;

/**
 * Status possíveis de uma sessão de usuário.
 * ACTIVE   — sessão criada e ainda válida
 * REVOKED  — revogada manualmente (logout, admin, troca de senha)
 * EXPIRED  — passou da dtExpiracao sem revogação explícita
 */
public enum SessionStatus {
    ACTIVE,
    REVOKED,
    EXPIRED
}
