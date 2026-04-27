package br.sptrans.scd.shared.audit;

/**
 * Contexto de auditoria por thread (ThreadLocal).
 *
 * Populado pelo {@link AuditContextFilter} no início de cada request HTTP:
 *  - requestId  → UUID gerado por request (traceId no MDC)
 *  - ipAddress  → IP de origem
 *  - userAgent  → navegador/cliente
 *
 * Enriquecido pelo {@link br.sptrans.scd.auth.adapter.in.web.filter.JwtAuthFilter}
 * após validação do JWT:
 *  - userId    → ID do usuário autenticado
 *  - sessionId → ID da sessão ativa
 *
 * Limpo pelo {@link AuditContextFilter} ao final do request.
 */
public final class AuditContext {

    private static final ThreadLocal<Data> HOLDER = ThreadLocal.withInitial(Data::new);

    private AuditContext() {
    }

    public static Data get() {
        return HOLDER.get();
    }

    /** Chamado pelo AuditContextFilter para inicializar o contexto da request. */
    public static void init(String requestId, String ipAddress, String userAgent) {
        Data d = HOLDER.get();
        d.requestId = requestId;
        d.ipAddress = ipAddress;
        d.userAgent = userAgent;
    }

    /** Chamado pelo JwtAuthFilter após autenticação bem-sucedida. */
    public static void setAuth(Long userId, String sessionId) {
        Data d = HOLDER.get();
        d.userId    = userId;
        d.sessionId = sessionId;
    }

    /** Remove o contexto ao final do request para evitar vazamento entre threads. */
    public static void clear() {
        HOLDER.remove();
    }

    /**
     * Dados do contexto de auditoria para a thread corrente.
     * Campos públicos para acesso direto sem boilerplate.
     */
    public static class Data {
        public String requestId;
        public Long   userId;
        public String sessionId;
        public String ipAddress;
        public String userAgent;
    }
}
