package br.sptrans.scd.audit.adapter.out.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.audit.adapter.out.jpa.entity.AuditLogEntityJpa;

/**
 * Spring Data JPA repository para {@link AuditLogEntityJpa}.
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntityJpa, Long> {

    List<AuditLogEntityJpa> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<AuditLogEntityJpa> findBySessionIdOrderByCreatedAtDesc(String sessionId);
}
