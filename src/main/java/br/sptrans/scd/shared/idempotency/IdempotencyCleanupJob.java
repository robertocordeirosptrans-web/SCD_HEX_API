package br.sptrans.scd.shared.idempotency;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Job agendado para limpeza de registros de idempotência expirados (TTL).
 *
 * <p>Remove da tabela {@code SPTRANSDBA.IDEMPOTENCY_LOG} todos os registros cuja
 * {@code CREATED_AT} seja anterior ao período de retenção configurado em
 * {@code idempotency.ttl-hours}.</p>
 *
 * <p>O cron é configurável via {@code idempotency.cleanup-cron}
 * (padrão: {@code 0 0 * * * *} — a cada hora).</p>
 */
@Component
@RequiredArgsConstructor
public class IdempotencyCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyCleanupJob.class);

    private final IdempotencyTxHelper txHelper;

    @Value("${idempotency.ttl-hours:24}")
    private int ttlHours;

    @Scheduled(cron = "${idempotency.cleanup-cron:0 0 * * * *}")
    public void cleanExpiredRecords() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(ttlHours);
        log.info("[idempotency-cleanup] Iniciando limpeza de registros expirados (cutoff={})", cutoff);
        try {
            int deleted = txHelper.deleteExpiredRecords(cutoff);
            log.info("[idempotency-cleanup] Limpeza concluída: {} registros removidos", deleted);
        } catch (Exception e) {
            log.error("[idempotency-cleanup] Erro durante a limpeza de registros expirados", e);
        }
    }
}
