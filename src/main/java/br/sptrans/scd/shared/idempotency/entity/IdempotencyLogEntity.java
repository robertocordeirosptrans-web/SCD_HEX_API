package br.sptrans.scd.shared.idempotency.entity;

import java.time.LocalDateTime;

import br.sptrans.scd.shared.idempotency.IdempotencyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA para a tabela {@code SPTRANSDBA.IDEMPOTENCY_LOG}.
 *
 * <p>Cada linha representa uma chave de idempotência com seu estado
 * ({@link IdempotencyStatus}) e a resposta serializada em JSON (quando SUCCESS).</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "IDEMPOTENCY_LOG", schema = "SPTRANSDBA")
public class IdempotencyLogEntity {

    @Id
    @Column(name = "IDEMPOTENCY_KEY", length = 255, nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(name = "REQUEST_HASH", length = 64)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private IdempotencyStatus status;

    @Lob
    @Column(name = "RESPONSE_BODY")
    private String responseBody;

    @Column(name = "HTTP_STATUS")
    private Integer httpStatus;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

 

  
}
