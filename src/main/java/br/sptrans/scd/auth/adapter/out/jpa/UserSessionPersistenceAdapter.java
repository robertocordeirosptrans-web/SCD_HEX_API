package br.sptrans.scd.auth.adapter.out.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.adapter.out.jpa.repository.UserSessionJpaRepository;
import br.sptrans.scd.auth.adapter.out.persistence.entity.UserSessionEntityJpa;
import br.sptrans.scd.auth.application.port.out.UserSessionRepository;
import br.sptrans.scd.auth.domain.session.UserSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Adapter de saída — faz a ponte entre o port UserSessionRepository
 * e o repositório JPA Oracle, aplicando cache Caffeine nas leituras.
 */
@Repository
@RequiredArgsConstructor
public class UserSessionPersistenceAdapter implements UserSessionRepository {

    private static final Logger log = LoggerFactory.getLogger(UserSessionPersistenceAdapter.class);

    private final UserSessionJpaRepository jpaRepository;

    @Override
    @Transactional
    @CacheEvict(value = "sessoes", key = "#session.idSessao")
    public UserSession save(UserSession session) {
        UserSessionEntityJpa entity = toEntity(session);
        UserSessionEntityJpa saved = jpaRepository.save(entity);
        log.debug("Sessão persistida: id={}, usuario={}", saved.getIdSessao(), saved.getIdUsuario());
        return toDomain(saved);
    }

    @Override
    @Cacheable(value = "sessoes", key = "#sessionId", unless = "#result == null")
    public Optional<UserSession> findBySessionId(String sessionId) {
        return jpaRepository.findById(sessionId).map(this::toDomain);
    }

    @Override
    public List<UserSession> findActiveByUserId(Long userId) {
        return jpaRepository
                .findByIdUsuarioAndDtRevogacaoIsNullAndDtExpiracaoAfter(userId, LocalDateTime.now())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "sessoes", key = "#sessionId")
    public void revokeBySessionId(String sessionId, Long revokedBy, String reason) {
        jpaRepository.findById(sessionId).ifPresentOrElse(
            entity -> {
                entity.setDtRevogacao(LocalDateTime.now());
                entity.setMotivoRevogacao(reason);
                entity.setIdRevogadoPor(revokedBy);
                jpaRepository.save(entity);
                log.info("Sessão revogada: id={}, motivo={}, revokedBy={}", sessionId, reason, revokedBy);
            },
            () -> log.warn("Tentativa de revogar sessão inexistente: id={}", sessionId)
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "sessoes", allEntries = true)
    public void revokeAllByUserId(Long userId, String reason) {
        int count = jpaRepository.revokeAllActiveByUserId(userId, LocalDateTime.now(), reason);
        log.info("Sessões revogadas em massa: usuario={}, motivo={}, total={}", userId, reason, count);
    }

    @Override
    @Transactional
    public int expireSessions() {
        int count = jpaRepository.expireSessions(LocalDateTime.now());
        if (count > 0) {
            log.info("Job de expiração: {} sessões marcadas como EXPIRED", count);
        }
        return count;
    }

    // ── Mapeamento entity <-> domínio ─────────────────────────────────────────

    private UserSession toDomain(UserSessionEntityJpa e) {
        UserSession s = new UserSession(
                e.getIdUsuario(),
                e.getEnderecoIp(),
                e.getAgenteUsuario(),
                0   // TTL ignorado — datas vêm do banco
        );
        // Sobrescreve os valores gerados pelo construtor com os vindos do banco
        s.setIdSessao(e.getIdSessao());
        s.setDtCriacao(e.getDtCriacao());
        s.setDtExpiracao(e.getDtExpiracao());
        s.setDtRevogacao(e.getDtRevogacao());
        s.setMotivoRevogacao(e.getMotivoRevogacao());
        s.setIdRevogadoPor(e.getIdRevogadoPor());
        return s;
    }

    private UserSessionEntityJpa toEntity(UserSession s) {
        return new UserSessionEntityJpa(
                s.getIdSessao(),
                s.getIdUsuario(),
                s.getEnderecoIp(),
                s.getAgenteUsuario(),
                s.getDtCriacao(),
                s.getDtExpiracao(),
                s.getDtRevogacao(),
                s.getMotivoRevogacao(),
                s.getIdRevogadoPor()
        );
    }
}
