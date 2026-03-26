package br.sptrans.scd.creditrequest.adapter.port.in.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.application.port.in.ReleaseRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
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

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestItemsRepository itemRepository;
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
    @Value("${scheduler.liberar-recarga.tamanho-lote:2}")
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

        List<CreditRequest> solicitacoes = creditRequestRepository.findElegiveisParaLiberacao(
                SituationCreditRequest.PAGO.getCode(),
                dtInicio,
                dtFim,
                tamanhoLote);

        int totalEncontradas = solicitacoes.size();
        int processadas = 0;
        int ignoradas = 0;

        for (CreditRequest solicitacao : solicitacoes) {
            Long numSolicitacao = solicitacao.getNumSolicitacao();
            String codCanal = solicitacao.getCodCanal();
            String numLote = solicitacao.getNumLote();

            // Revalida condições de elegibilidade (evita corrida com alteração manual)
            if (!SituationCreditRequest.PAGO.getCode().equals(solicitacao.getCodSituacao())) {
                log.debug("Solicitação {}/{} ignorada: status atual é {}", numSolicitacao, codCanal, solicitacao.getCodSituacao());
                ignoradas++;
                continue;
            }
            if ("S".equals(solicitacao.getFlgCanc())) {
                log.debug("Solicitação {}/{} ignorada: flgCanc=S", numSolicitacao, codCanal);
                ignoradas++;
                continue;
            }
            if ("S".equals(solicitacao.getFlgBloq())) {
                log.debug("Solicitação {}/{} ignorada: flgBloq=S", numSolicitacao, codCanal);
                ignoradas++;
                continue;
            }

            if (!possuiItensElegiveis(solicitacao)) {
                log.debug("Solicitação {}/{} ignorada: sem itens elegíveis em PAGO", numSolicitacao, codCanal);
                ignoradas++;
                continue;
            }

            try {
                releaseRechargeUseCase.liberarRecargaPorSolicitacao(numSolicitacao,numLote, codCanal);
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
        

        List<CreditRequestItemsEJpa> itens = itemRepository.findProcessRechargeService(solicitacao.getNumSolicitacao(), solicitacao.getCodCanal(), solicitacao.getNumLote());
        if (itens == null || itens.isEmpty()) {
            return false;
        }
        String codPago = SituationCreditRequestItems.PAGO.getCode();
        return itens.stream().anyMatch(item -> codPago.equals(item.getCodSituacao()));
    }
}
