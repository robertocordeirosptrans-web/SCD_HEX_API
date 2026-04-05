package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.port.out.jpa.repository.UserRepositoryJpa;
import br.sptrans.scd.auth.application.port.out.UserStatusPort;

import lombok.RequiredArgsConstructor;

/**
 * Adapter — Repositório de Status e Jornada do Usuário
 * 
 * Responsável por: - Atualizar status de usuário (ativo/inativo)
 * - Atualizar senha com histórico - Gerenciar jornada de acesso
 * 
 * Implementa: UserStatusRepository
 */
@Primary
@Repository
@RequiredArgsConstructor
public class UserStatusRepositoryAdapter implements UserStatusPort {

    private static final Logger log = LoggerFactory.getLogger(UserStatusRepositoryAdapter.class);

    private final UserRepositoryJpa userRepositoryJpa;

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public void updateStatus(Long idUsuario, String codStatus, Long idUsuarioManutencao) {
        log.debug("Atualizando status de usuário: id={}, novo_status={}, atualizado_por={}", 
            idUsuario, codStatus, idUsuarioManutencao);
        userRepositoryJpa.updateStatus(idUsuario, codStatus);
    }

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public void updatePassword(Long idUsuario, String newPasswordHash, String oldPasswordHash, LocalDateTime expiryDate) {
        log.debug("Atualizando senha de usuário: id={}, expira_em={}", idUsuario, expiryDate);
        userRepositoryJpa.updatePassword(idUsuario, newPasswordHash, oldPasswordHash, expiryDate);
    }

    @Override
    @CacheEvict(value = "permissoes", allEntries = true)
    public void updateAccessSchedule(Long idUsuario, String diasPermitidos, Date jornadaIni, Date jornadaFim, Long idUsuarioManutencao) {
        log.debug("Atualizando jornada de acesso: id={}, dias={}, ini={}, fim={}, atualizado_por={}", 
            idUsuario, diasPermitidos, jornadaIni, jornadaFim, idUsuarioManutencao);
        userRepositoryJpa.updateAccessSchedule(idUsuario, diasPermitidos, jornadaIni, jornadaFim);
    }
}
