package br.sptrans.scd.auth.domain.vo;

import java.time.LocalDateTime;

import br.sptrans.scd.auth.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Value;

/**
 * Value Object imutável: status e dados de auditoria do usuário.
 */
@Value
@Builder(toBuilder = true)
public class UserAudit {

    UserStatus codStatus;
    LocalDateTime dtCriacao;
    LocalDateTime dtModi;
    LocalDateTime dtUltimoAcesso;
    Long idUsuarioManutencao;

    public boolean isAtivo() {
        return codStatus == UserStatus.ACTIVE;
    }

    public boolean isBloqueado() {
        return codStatus == UserStatus.BLOCKED;
    }

    public boolean isInativo() {
        return codStatus == UserStatus.INACTIVE;
    }

    public UserAudit comStatus(UserStatus novoStatus, Long idManutencao) {
        return toBuilder()
                .codStatus(novoStatus)
                .dtModi(LocalDateTime.now())
                .idUsuarioManutencao(idManutencao)
                .build();
    }

    public UserAudit registrarAcesso() {
        return toBuilder().dtUltimoAcesso(LocalDateTime.now()).build();
    }
}
