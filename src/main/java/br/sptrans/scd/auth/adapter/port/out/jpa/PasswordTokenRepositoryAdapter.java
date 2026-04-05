package br.sptrans.scd.auth.adapter.port.out.jpa;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.auth.adapter.port.out.jpa.mapper.PasswordTokenMapper;
import br.sptrans.scd.auth.adapter.port.out.jpa.repository.PasswordTokenJpaRepository;
import br.sptrans.scd.auth.adapter.port.out.persistence.entity.PasswordTokenEntityJpa;
import br.sptrans.scd.auth.application.port.out.PasswordTokenPort;
import br.sptrans.scd.auth.domain.PasswordResetToken;
import lombok.RequiredArgsConstructor;

/**
 * Adapter — Repositório de Tokens de Redefinição de Senha
 * 
 * Responsável por: - Persistir novos tokens de redefinição - Buscar tokens por UUID
 * - Invalidar tokens expirados ou já usados - Marcar tokens como usados
 * 
 * Implementa: PasswordTokenPort (interface de negócio, não JpaRepository)
 */
@Repository
@RequiredArgsConstructor
public class PasswordTokenRepositoryAdapter implements PasswordTokenPort {

    private static final Logger log = LoggerFactory.getLogger(PasswordTokenRepositoryAdapter.class);

    private final PasswordTokenJpaRepository tokenJpaRepository;

    @Override
    @Transactional
    public void save(PasswordResetToken token) {
        log.debug("Persistindo token de redefinição de senha para usuário: {}", token.getIdUsuario());
        PasswordTokenEntityJpa entity = PasswordTokenMapper.toEntity(token);
        tokenJpaRepository.save(entity);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        log.debug("Buscando token de redefinição de senha: {}", token);
        return tokenJpaRepository.findByToken(token)
            .map(PasswordTokenMapper::toDomain);
    }

    @Override
    public Optional<PasswordResetToken> findByIdUsuarioAndUsedFalse(Long idUsuario) {
        log.debug("Buscando token ativo (não usado) para usuário: {}", idUsuario);
        return tokenJpaRepository.findByIdUsuarioAndUsedFalse(idUsuario)
            .map(PasswordTokenMapper::toDomain);
    }

    @Override
    @Transactional
    public void invalidateTokensForUser(Long idUsuario) {
        log.debug("Invalidando tokens de redefinição para usuário: {}", idUsuario);
        tokenJpaRepository.invalidateTokensForUser(idUsuario);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        log.debug("Deletando tokens expirados");
        tokenJpaRepository.deleteExpiredTokens();
    }
}
