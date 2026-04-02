package br.sptrans.scd.auth.application.port.out;

/**
 * Porto de saída — rastreamento de tentativas e sessão (autenticação).
 * <p>Segregado conforme ISP: agrupa apenas operações relacionadas ao ciclo
 * de login/falha/bloqueio, separadas de leitura e escrita de dados cadastrais.</p>
 */
public interface AuthenticationRepository {

    /**
     * Atualiza o contador de tentativas falhas e o status da conta
     * (ex.: bloqueia após N falhas).
     */
    void atualizarTentativasEStatus(Long idUsuario, int numTentativas, String codStatus);

    /**
     * Registra a data/hora do último acesso bem-sucedido.
     */
    void atualizarUltimoAcesso(Long idUsuario);

    /**
     * Zera o contador de tentativas e aplica o novo status
     * (uso em reativação/desbloqueio).
     */
    void resetAttemptsAndStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao);

    /**
     * Verifica se o usuário possui sessão ativa nos últimos 30 minutos.
     */
    boolean hasActiveSession(Long idUsuario);
}
