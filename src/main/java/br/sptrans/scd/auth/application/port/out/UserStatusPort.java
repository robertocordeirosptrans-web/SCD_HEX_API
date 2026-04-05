package br.sptrans.scd.auth.application.port.out;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Porta de Saída — atualizações de status, senha e jornada de acesso.
 * <p>Segregado conforme ISP: agrupa mutações de estado administrativo do
 * usuário, separadas de leitura e de rastreamento de autenticação.</p>
 */
public interface UserStatusPort {

    /**
     * Atualiza {@code COD_STATUS} e {@code DT_MANUTENCAO}.
     */
    void updateStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao);

    /**
     * Atualiza {@code COD_SENHA}, {@code OLD_SENHA} e {@code DT_EXPIRA_SENHA}.
     */
    void updatePassword(Long idUsuario, String newPasswordHash, String oldPasswordHash, LocalDateTime expiryDate);

    /**
     * Atualiza jornada de acesso ({@code NUM_DIAS_SEMANAS_PERMITIDOS},
     * {@code DT_JORNADA_INI}, {@code DT_JORNADA_FIM}).
     */
    void updateAccessSchedule(Long idUsuario, String diasPermitidos, Date jornadaIni, Date jornadaFim, Long idUsuarioManutencao);
}
