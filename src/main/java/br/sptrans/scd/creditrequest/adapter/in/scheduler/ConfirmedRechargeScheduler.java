package br.sptrans.scd.creditrequest.adapter.in.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.application.port.in.ConfirmedRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Job agendado responsável por confirmar o retorno do Hardware Manager para
 * pedidos em {@code EM_PROCESSO_DE_RECARGA (06)}, atualizando os itens
 * confirmados para {@code RECARREGADO (07)} e recalculando a situação do
 * pedido.
 *
 * <p>
 * O job:</p>
 * <ol>
 * <li>Busca solicitações em {@code EM_PROCESSO_DE_RECARGA (06)}.</li>
 * <li>Para cada solicitação, chama
 * {@link ConfirmarRecargaUseCase#confirmarRecarga}.</li>
 * </ol>
 *
 * <p>
 * Executado a cada {@code scheduler.confirmar-recarga.intervalo-ms}
 * milissegundos (padrão: 30 000 ms = 30 segundos).</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConfirmedRechargeScheduler {

    private final CreditRequestItemsPort repository;
    private final ConfirmedRechargeUseCase confirmarRecargaUseCase;

    @Value("${scheduler.confirmar-recarga.enabled:true}")
    private boolean enabled;

    /**
     * Tamanho de cada lote de solicitações processadas por execução.
     */
    @Value("${scheduler.confirmar-recarga.tamanho-lote:100}")
    private int tamanhoLote;

    /**
     * Executa a confirmação de recarga periodicamente. O intervalo é
     * configurável via {@code scheduler.confirmar-recarga.intervalo-ms}.
     */
    @Scheduled(fixedRateString = "${scheduler.confirmar-recarga.intervalo-ms:30000}")
    public void executar() {
        if (!enabled) {
            log.debug("Job ConfirmarRecarga desativado (scheduler.confirmar-recarga.enabled=false)");
            return;
        }
        log.debug("Iniciando job ConfirmarRecarga");

        List<CreditRequestItems> itensElegiveis = repository.searchItemsToBeConfirmed(SituationCreditRequest.EM_PROCESSO_DE_RECARGA.getCode(), tamanhoLote);

        int totalEncontradas = itensElegiveis.size();
        int processadas = 0;
        int ignoradas = 0;

        for (CreditRequestItems item : itensElegiveis) {


            try {
                confirmarRecargaUseCase.confirmarRecarga(item);
                processadas++;
                log.info("Confirmação de recarga processada para solicitação {}/{}.", item.getId().getNumSolicitacao(), item.getId().getCodCanal());
            } catch (Exception e) {
                log.error("Erro ao confirmar recarga para solicitação {}/{}: {}",
                        item.getId().getNumSolicitacao(), item.getId().getCodCanal(), e.getMessage(), e);
                ignoradas++;
            }
        }

        log.info("Job ConfirmarRecarga concluído. Encontradas={}, Processadas={}, Ignoradas={}",
                totalEncontradas, processadas, ignoradas);
    }
}
