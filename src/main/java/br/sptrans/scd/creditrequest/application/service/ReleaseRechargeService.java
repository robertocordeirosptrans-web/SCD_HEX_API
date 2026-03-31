package br.sptrans.scd.creditrequest.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.application.port.in.ReleaseRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do use case de liberação de recarga.
 *
 * <p>
 * Transição dos itens: PAGO(04) → LIBERADO_PARA_RECARGA(05).</p>
 * <p>
 * Recalcula a situação consolidada do pedido após a liberação.</p>
 */
@Service
@RequiredArgsConstructor
public class ReleaseRechargeService implements ReleaseRechargeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReleaseRechargeService.class);
    private static final String ORIGEM_TRANSICAO = "liberar_recarga_scd";

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestItemsRepository itemRepository;
    private final HistCreditRequestService historyService;
    private final SituationAscertainedService situationAscertainedService;



    @Override
    @Transactional
    public void liberarRecarga(ReleaseRechargeCommand comando) {
        CreditRequest solicitacao = creditRequestRepository
                .findByCodTipoDocumentoAndIdUsuarioCadastro(
                        comando.codTipoDocumento(), comando.idUsuarioCadastro())
                .orElse(null);

        if (solicitacao == null) {
            log.warn("Solicitação não encontrada para codTipoDocumento={}, idUsuarioCadastro={}",
                    comando.codTipoDocumento(), comando.idUsuarioCadastro());
            return;
        }

        String origemTransicao = comando.idOrigemTransicao() != null
                ? comando.idOrigemTransicao() : ORIGEM_TRANSICAO;

        liberarRecargaPorSolicitacao(solicitacao.getNumSolicitacao(), solicitacao.getCodCanal(), origemTransicao);
    }

    @Override
    @Transactional
    public int liberarRecargaEmLote(BatchReleaseCommand comando) {
        List<CreditRequest> solicitacoes = creditRequestRepository
                .findByCanalAndSituacao(null, SituationCreditRequest.PAGO.getCode());

        int liberadas = 0;
        for (CreditRequest solicitacao : solicitacoes) {
            try {
                liberarRecargaPorSolicitacao(solicitacao.getNumSolicitacao(), solicitacao.getNumLote(), solicitacao.getCodCanal());
                liberadas++;
            } catch (Exception e) {
                log.error("Erro ao liberar recarga para solicitação {}/{}: {}",
                        solicitacao.getNumSolicitacao(), solicitacao.getCodCanal(), e.getMessage(), e);
            }
        }

        log.info("Liberação em lote concluída. Liberadas={}", liberadas);
        return liberadas;
    }

    @Override
    @Transactional
    public void liberarRecargaPorSolicitacao(Long numSolicitacao,String numLote, String codCanal) {
        liberarRecargaPorSolicitacao(numSolicitacao, codCanal, numLote, ORIGEM_TRANSICAO);
    }

    @Transactional
    public void liberarRecargaPorSolicitacao(Long numSolicitacao, String codCanal, String numLote, String origemTransicao) {
        log.debug("Liberando recarga para solicitação {}/{} (novo método SQL)", numSolicitacao, codCanal);

        // Busca o primeiro item elegível conforme a nova query SQL
        String codSituacao = "04";
        // Para obter a data de pagamento econômica, normalmente seria necessário buscar na solicitação ou item
        // Aqui, vamos assumir que o campo dtPagtoEconomica está disponível na solicitação (ajuste se necessário)
        CreditRequest solicitacao = creditRequestRepository.findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal).orElse(null);
        if (solicitacao == null || solicitacao.getDtPagtoEconomica() == null) {
            log.warn("Solicitação ou data de pagamento econômica não encontrada para {}/{}", numSolicitacao, codCanal);
            return;
        }
        java.sql.Timestamp dtInicio = java.sql.Timestamp.valueOf(solicitacao.getDtPagtoEconomica());
        java.sql.Timestamp dtFim = java.sql.Timestamp.valueOf(solicitacao.getDtPagtoEconomica());
        List<CreditRequestItemsEJpa> itens = itemRepository.findFirstBySituacaoAndDtPagtoEconomicaBetween(codSituacao, dtInicio, dtFim,100);
        if (itens == null || itens.isEmpty()) {
            log.warn("Nenhum item elegível encontrado para solicitação {}/{} na data {}", numSolicitacao, codCanal, solicitacao.getDtPagtoEconomica());
            return;
        }

        int liberados = 0;
        for (CreditRequestItemsEJpa itemEJpa : itens) {
            CreditRequestItemsKey key = new CreditRequestItemsKey();
            key.setNumSolicitacao(itemEJpa.getId().getNumSolicitacao());
            key.setNumSolicitacaoItem(itemEJpa.getId().getNumSolicitacaoItem());
            key.setCodCanal(itemEJpa.getId().getCodCanal());

            var optItem = itemRepository.findById(key);
            if (optItem.isEmpty()) {
                continue;
            }
            CreditRequestItems item = optItem.get();
            if (!SituationCreditRequestItems.PAGO.getCode().equals(item.getCodSituacao())) {
                continue;
            }
            item.setCodSituacao(SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode());
            item.setDtManutencao(LocalDateTime.now());
            itemRepository.save(item);
            historyService.saveItemStatusHistory(item, origemTransicao);
            liberados++;
            log.info("Item liberado - Solicitação={}, Item={}, NovoStatus={}",
                    numSolicitacao,
                    item.getId().getNumSolicitacaoItem(),
                    SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode());
        }

        if (liberados > 0) {
            consolidarStatusSolicitacao(numSolicitacao, codCanal, itens, origemTransicao);
        }

        log.debug("Liberação de recarga concluída para solicitação {}/{} - {} itens liberados",
                numSolicitacao, codCanal, liberados);
    }

    private void consolidarStatusSolicitacao(Long numSolicitacao, String codCanal,
            List<CreditRequestItemsEJpa> itens, String origemTransicao) {

        CreditRequest solicitacao = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal)
                .orElse(null);

        if (solicitacao == null) {
            log.warn("Solicitação {}/{} não encontrada para consolidação", numSolicitacao, codCanal);
            return;
        }

        List<String> statusItens = itens.stream()
                .map(CreditRequestItemsEJpa::getCodSituacao)
                .filter(Objects::nonNull)
                .toList();

        String novoStatus = situationAscertainedService.apurarSituacaoPedido(statusItens);

        if (novoStatus != null && !novoStatus.equals(solicitacao.getCodSituacao())) {
            String statusAnterior = solicitacao.getCodSituacao();
            solicitacao.setCodSituacao(novoStatus);
            creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
            historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, origemTransicao);

            log.info("Status da solicitação {}/{} consolidado - StatusAnterior={}, NovoStatus={}",
                    numSolicitacao, codCanal, statusAnterior, novoStatus);
        }
    }


}
