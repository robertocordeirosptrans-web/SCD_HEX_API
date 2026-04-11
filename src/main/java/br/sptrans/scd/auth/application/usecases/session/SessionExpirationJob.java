package br.sptrans.scd.auth.application.usecases.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.sptrans.scd.auth.application.port.in.SessionManagementUseCase;
import lombok.RequiredArgsConstructor;

/**
 * Job agendado de expiração de sessões.
 *
 * <p>Executa periodicamente e marca no banco todas as sessões
 * cujo DT_EXPIRACAO já passou, prevenindo acúmulo de registros
 * órfãos e mantendo relatórios de sessão coerentes.</p>
 *
 * <p>O filtro JWT já rejeita sessões expiradas em tempo real
 * (via {@code isAtiva()}); este job apenas sincroniza o banco.</p>
 */
@Component
@RequiredArgsConstructor
public class SessionExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(SessionExpirationJob.class);

    private final SessionManagementUseCase sessionUseCase;

    @Value("${session.expiration-job.enabled:true}")
    private boolean enabled;

    /**
     * Executa a cada hora por padrão.
     * Configurável via {@code session.expiration-job.cron}.
     */
    @Scheduled(cron = "${session.expiration-job.cron:0 0 * * * *}")
    public void expireSessions() {
        if (!enabled) {
            log.debug("Job de expiração de sessões desabilitado.");
            return;
        }
        log.debug("Iniciando job de expiração de sessões...");
        int count = sessionUseCase.expireSessions();
        if (count > 0) {
            log.info("Job de expiração: {} sessão(ões) marcada(s) como EXPIRED.", count);
        } else {
            log.debug("Job de expiração: nenhuma sessão para expirar.");
        }
    }
}
