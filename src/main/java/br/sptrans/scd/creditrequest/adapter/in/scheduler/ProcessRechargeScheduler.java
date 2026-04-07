package br.sptrans.scd.creditrequest.adapter.in.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.application.port.in.ProcessRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.in.ProcessRechargeUseCase.ProcessRechargeCommand;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Job agendado responsável por processar pedidos em
 * {@code LIBERADO_PARA_RECARGA (05)}, enviando-os ao Hardware Manager ou
 * marcando-os diretamente como recarregados quando o valor do evento é zero.
 *
 * <p>O job:</p>
 * <ol>
 *   <li>Busca solicitações em {@code LIBERADO_PARA_RECARGA (05)}.</li>
 *   <li>Para cada solicitação, chama
 *       {@link ProcessRechargeUseCase#processarRecarga}.</li>
 * </ol>
 *
 * <p>Executado a cada {@code scheduler.processar-recarga.intervalo-ms}
 * milissegundos (padrão: 60 000 ms = 1 minuto).</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessRechargeScheduler {

    private final CreditRequestPort creditRequestRepository;
    private final ProcessRechargeUseCase processRechargeUseCase;

    @Value("${scheduler.processar-recarga.enabled:true}")
    private boolean enabled;

    /**
     * Tamanho de cada lote de solicitações processadas por execução.
     */
    @Value("${scheduler.processar-recarga.tamanho-lote:100}")
    private int tamanhoLote;

    /**
     * Executa o processamento de recarga periodicamente. O intervalo é
     * configurável via {@code scheduler.processar-recarga.intervalo-ms}.
     */
    @Scheduled(fixedRateString = "${scheduler.processar-recarga.intervalo-ms:60000}")
    public void executar() {
        if (!enabled) {
            log.debug("Job ProcessarRecarga desativado (scheduler.processar-recarga.enabled=false)");
            return;
        }
        log.debug("Iniciando job ProcessarRecarga");

        int processadas = 0;
        int ignoradas = 0;

        for (int i = 0; i < tamanhoLote; i++) {
            CreditRequest solicitacao = creditRequestRepository.findElegiveisParaProcessamento(
                    SituationCreditRequest.LIBERADO_PARA_RECARGA.getCode());

            if (solicitacao == null) {
                break;
            }

            try {
                ProcessRechargeCommand comando = new ProcessRechargeCommand(
                        solicitacao.getCodTipoDocumento(),
                        solicitacao.getIdUsuarioCadastro(),
                        solicitacao.getSqPid());

                processRechargeUseCase.processarRecarga(comando);
                processadas++;
                log.info("Recarga processada para solicitação {}/{}.",
                        solicitacao.getNumSolicitacao(), solicitacao.getCodCanal());
            } catch (Exception e) {
                log.error("Erro ao processar recarga para solicitação {}/{}: {}",
                        solicitacao.getNumSolicitacao(), solicitacao.getCodCanal(), e.getMessage(), e);
                ignoradas++;
            }
        }

        log.info("Job ProcessarRecarga concluído. Processadas={}, Ignoradas={}",
                processadas, ignoradas);
    }
}
