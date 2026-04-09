package br.sptrans.scd.creditrequest.adapter.in.scheduler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.creditrequest.application.port.in.ProcessRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.in.ProcessRechargeUseCase.ProcessRechargeCommand;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
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


        Map<String, CreditRequestItems> solicitacoesUnicas = new LinkedHashMap<>();
        for (CreditRequestItems item : itens) {
            String chave = item.getCodTipoDocumento() + "_" + item.getIdUsuarioCadastro();
            solicitacoesUnicas.putIfAbsent(chave, item);
            if (solicitacoesUnicas.size() >= tamanhoLote) {
                break;
            }
        }

        int processadas = 0;
        int ignoradas = 0;

        for (CreditRequestItems item : solicitacoesUnicas.values()) {
            try {
                ProcessRechargeCommand comando = new ProcessRechargeCommand(
                        item.getCodTipoDocumento(),
                        item.getIdUsuarioCadastro(),
                        item.getSqPid());

                processRechargeUseCase.processarRecarga(comando);
                processadas++;
                log.info("Recarga processada para codTipoDocumento={}, idUsuarioCadastro={}.",
                        item.getCodTipoDocumento(), item.getIdUsuarioCadastro());
            } catch (Exception e) {
                log.error("Erro ao processar recarga para codTipoDocumento={}, idUsuarioCadastro={}: {}",
                        item.getCodTipoDocumento(), item.getIdUsuarioCadastro(), e.getMessage(), e);
                ignoradas++;
            }
        }

        log.info("Job ProcessarRecarga concluído. Processadas={}, Ignoradas={}",
                processadas, ignoradas);
    }
}

