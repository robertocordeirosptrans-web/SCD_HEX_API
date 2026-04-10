package br.sptrans.scd.creditrequest.adapter.in.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.service.ProcessRechargeService;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Job agendado responsável por processar pedidos em
 * {@code LIBERADO_PARA_RECARGA (05)}, enviando-os ao Hardware Manager ou
 * marcando-os diretamente como recarregados quando o valor do evento é zero.
 *
 * <p>O job:</p>
 * <ol>
 *   <li>Busca itens em {@code LIBERADO_PARA_RECARGA (05)} via
 *       {@code searchItemsToBeProcessed}.</li>
 *   <li>Deduplica por solicitação e chama
 *       {@link ProcessRechargeUseCase#processarRecarga} para cada uma.</li>
 * </ol>
 *
 * <p>Executado a cada {@code scheduler.processar-recarga.intervalo-ms}
 * milissegundos (padrão: 60 000 ms = 1 minuto).</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessRechargeScheduler {

    private final CreditRequestItemsPort itemRepository;
    private final ProcessRechargeService processRechargeService;


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
    @Transactional
    public void executar() {
        if (!enabled) {
            log.debug("Job ProcessarRecarga desativado (scheduler.processar-recarga.enabled=false)");
            return;
        }
        log.debug("Iniciando job ProcessarRecarga");

        List<CreditRequestItems> itens = itemRepository.searchItemsToBeProcessed(
                SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode());

        if (itens.isEmpty()) {
            log.debug("Nenhum item elegível para processamento encontrado.");
            return;
        }

        // Limita o tamanho do lote
        List<CreditRequestItems> lote = itens.size() > tamanhoLote ? itens.subList(0, tamanhoLote) : itens;

        try {
            processRechargeService.processarLoteRecarga(lote);
            log.info("Job ProcessarRecarga concluído. Processadas={}", lote.size());
        } catch (Exception e) {
            log.error("Erro ao processar lote de recarga: {}", e.getMessage(), e);
        }
    }
}

