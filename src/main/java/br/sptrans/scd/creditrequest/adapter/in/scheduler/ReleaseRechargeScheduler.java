package br.sptrans.scd.creditrequest.adapter.in.scheduler;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.application.port.in.ReleaseRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduler responsável por executar automaticamente o processo de "Liberar
 * Recarga" para solicitações que estão no status PAGO há pelo menos
 * {@code atrasoMinutos} minutos.
 *
 * <p>O job:</p>
 * <ol>
 *   <li>Busca solicitações em PAGO dentro de uma janela configurável.</li>
 *   <li>Filtra itens elegíveis (somente {@code SituationCreditRequestItems.PAGO}).</li>
 *   <li>Chama {@link ReleaseRechargeUseCase#liberarRecargaPorSolicitacao} para cada
 *       solicitação com itens elegíveis.</li>
 * </ol>
 *
 * <p>O job é idempotente: após a liberação, a solicitação sai do status PAGO e não
 * será mais selecionada nas execuções seguintes.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ReleaseRechargeScheduler {

    private final CreditRequestItemsPort itemRepository;
    private final ReleaseRechargeUseCase releaseRechargeUseCase;

    @Value("${scheduler.liberar-recarga.enabled:true}")
    private boolean enabled;

    /**
     * Atraso mínimo (em minutos) após o pagamento antes de liberar.
     */
    @Value("${scheduler.liberar-recarga.atraso-minutos:3}")
    private long atrasoMinutos;

    /**
     * Janela de busca (em minutos) para trás a partir de (now - atraso).
     */
    @Value("${scheduler.liberar-recarga.janela-minutos:30}")
    private long janelaMinutos;

    /**
     * Tamanho de cada lote de solicitações processadas por execução.
     */
    @Value("${scheduler.liberar-recarga.tamanho-lote:100}")
    private int tamanhoLote;

    /**
     * Executa o processo de liberação de recarga periodicamente. O intervalo é
     * configurável via {@code scheduler.liberar-recarga.intervalo-ms}.
     */
    @Scheduled(fixedDelayString = "${scheduler.liberar-recarga.intervalo-ms:60000}")
    public void executar() {
        if (!enabled) {
            log.debug("Job LiberarRecarga desativado (scheduler.liberar-recarga.enabled=false)");
            return;
        }
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dtFim = agora.minusMinutes(atrasoMinutos);
        LocalDateTime dtInicio = dtFim.minusMinutes(janelaMinutos);

        log.debug("Iniciando job LiberarRecarga. Janela: [{} - {}]", dtInicio, dtFim);

        // Busca diretamente os itens elegíveis conforme a nova query
        String codSituacao = "04";
        List<CreditRequestItemsEJpa> itensElegiveis = itemRepository.findFirstBySituacaoAndDtPagtoEconomicaBetween(
                codSituacao,
                Timestamp.valueOf(dtInicio),
                Timestamp.valueOf(dtFim),
                tamanhoLote
        );

        int totalEncontradas = itensElegiveis != null ? itensElegiveis.size() : 0;
        int processadas = 0;
        int ignoradas = 0;

        if (itensElegiveis == null || itensElegiveis.isEmpty()) {
            log.info("Nenhum item elegível encontrado para liberação de recarga na janela informada.");
            return;
        }

        for (CreditRequestItemsEJpa item : itensElegiveis) {
            Long numSolicitacao = item.getId().getNumSolicitacao();
            String codCanal = item.getId().getCodCanal();
            String numLote = null; // Ajuste se necessário para obter o lote

            try {
                releaseRechargeUseCase.liberarRecargaPorSolicitacao(numSolicitacao, numLote, codCanal);
                processadas++;
                log.info("Solicitação {}/{} liberada para recarga.", numSolicitacao, codCanal);
            } catch (Exception e) {
                log.error("Erro ao liberar recarga para solicitação {}/{}: {}", numSolicitacao, codCanal, e.getMessage(), e);
                ignoradas++;
            }
        }

        log.info("Job LiberarRecarga concluído. Encontradas={}, Processadas={}, Ignoradas={}",
                totalEncontradas, processadas, ignoradas);
    }

    /**
     * Verifica se a solicitação possui ao menos um item em
     * {@link SituationCreditRequestItems#PAGO}.
     */
    boolean possuiItensElegiveis(CreditRequest solicitacao) {
        // Busca o primeiro item com situação '04' e data de pagamento econômica no intervalo
        String codSituacao = "04";
        Timestamp dtInicio = Timestamp.valueOf(solicitacao.getDtPagtoEconomica());
        Timestamp dtFim = Timestamp.valueOf(solicitacao.getDtPagtoEconomica());
        List<CreditRequestItemsEJpa> itens = itemRepository.findFirstBySituacaoAndDtPagtoEconomicaBetween(codSituacao, dtInicio, dtFim,tamanhoLote);
        return itens != null && !itens.isEmpty();
    }
}
