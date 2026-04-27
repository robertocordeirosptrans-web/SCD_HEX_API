package br.sptrans.scd.auth.adapter.out.jpa;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.jpa.repository.UserRepositoryJpa;
import br.sptrans.scd.auth.application.port.out.AuthenticationPort;

import lombok.RequiredArgsConstructor;

/**
 * Adapter — Repositório de Rastreamento de Autenticação
 * 
 * Responsável por: - Atualizar tentativas falhas após login inválido
 * - Registrar último acesso bem-sucedido - Resetar tentativas em desbloqueio -
 * Verificar sessões ativas
 * 
 * Implementa: AuthenticationPort
 */
@Primary
@Repository
@RequiredArgsConstructor
public class AuthenticationAdapter implements AuthenticationPort {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationAdapter.class);

    private final UserRepositoryJpa userRepositoryJpa;

    @Override
    public void atualizarTentativasEStatus(Long idUsuario, int numTentativas, String codStatus) {
        log.debug("Atualizando tentativas falhas: usuário={}, tentativas={}, status={}", 
            idUsuario, numTentativas, codStatus);
        userRepositoryJpa.atualizarTentativasEStatus(idUsuario, numTentativas, codStatus);
    }

    @Override
    public void atualizarUltimoAcesso(Long idUsuario) {
        log.debug("Registrando último acesso: usuário={}", idUsuario);
        userRepositoryJpa.atualizarUltimoAcesso(idUsuario);
    }

    @Override
    public void resetAttemptsAndStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
        log.debug("Resetando tentativas e atualizando status: usuário={}, novo_status={}, reset_por={}", 
            idUsuario, codStatus, idUsuarioManutencao);
        userRepositoryJpa.resetAttemptsAndStatus(idUsuario, codStatus);
    }

    @Override
    public boolean hasActiveSession(Long idUsuario) {
        log.debug("Verificando sessão ativa para usuário={}", idUsuario);
        return userRepositoryJpa.findById(idUsuario)
            .map(entity -> {
                if (entity.getDtUltimoAcesso() == null) {
                    return false;
                }
                boolean isActive = entity.getDtUltimoAcesso()
                    .isAfter(LocalDateTime.now().minusMinutes(30));
                log.debug("Sessão {}ativa para usuário={}", isActive ? "" : "in", idUsuario);
                return isActive;
            })
            .orElse(false);
    }
}
