package br.sptrans.scd.creditrequest.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.application.port.in.ConfirmedRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do use case de confirmação de recarga.
 *
 * <p>Equivalente à rotina {@code ConfirmarRecarga} da package Oracle
 * {@code PCK_MVE_SITUACAOPEDIDO}.</p>
 *
 * <p>Para cada item em {@code EM_PROCESSO_DE_RECARGA (06)} cujo retorno do
 * Hardware Manager já foi registrado ({@code DT_RETORNO_HM} preenchida),
 * atualiza o status para {@code RECARREGADO (07)} e recalcula a situação
 * consolidada do pedido.</p>
 */
@Service
@RequiredArgsConstructor
public class ConfirmedRechargeService implements ConfirmedRechargeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmedRechargeService.class);
    private static final String ORIGEM_TRANSICAO = "confirmar_recarga_scd";

    private final CreditRequestPort creditRequestRepository;
    private final CreditRequestItemsPort itemRepository;
    private final HistCreditRequestService historyService;
    private final SituationAscertainedService situationAscertainedService;



    @Override
    @Transactional
    public void confirmarRecarga(Long numSolicitacao, String codCanal) {
        log.debug("Confirmando recarga para solicitação {}/{}", numSolicitacao, codCanal);

        // Obtém o numLote da solicitação
        CreditRequest solicitacao = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal)
                .orElse(null);
        if (solicitacao == null) {
            log.warn("Solicitação não encontrada para numSolicitacao={}, codCanal={}", numSolicitacao, codCanal);
            return;
        }
        String numLote = solicitacao.getNumLote();
        List<Long> numSolicitacaoItens = itemRepository.findNumSolicitacaoItemsBySolicitacaoCanalLote(numSolicitacao, codCanal, numLote);
        if (numSolicitacaoItens == null || numSolicitacaoItens.isEmpty()) {
            log.warn("Nenhum item encontrado para solicitação {}/{} e lote {}", numSolicitacao, codCanal, numLote);
            return;
        }

        int confirmados = 0;
        List<CreditRequestItemsEJpa> itens = new ArrayList<>();
        for (Long numSolicitacaoItem : numSolicitacaoItens) {
            CreditRequestItemsKey key = new CreditRequestItemsKey();
            key.setNumSolicitacao(numSolicitacao);
            key.setNumSolicitacaoItem(numSolicitacaoItem);
            key.setCodCanal(codCanal);
            var optItem = itemRepository.findById(key);
            if (optItem.isEmpty()) continue;
            CreditRequestItems item = optItem.get();
            if (!SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.getCode().equals(item.getCodSituacao())) {
                continue;
            }
            if (item.getDtRetornoHm() == null) {
                continue;
            }
            String statusAnterior = item.getCodSituacao();
            item.setCodSituacao(SituationCreditRequestItems.RECARREGADO.getCode());
            item.setDtManutencao(java.time.LocalDateTime.now());
            itemRepository.save(item);
            historyService.saveItemStatusHistory(item, ORIGEM_TRANSICAO);
            confirmados++;
            // Adiciona para consolidação
            itens.add(toEntity(item));
            log.info("Item confirmado - Solicitação={}, Item={}, StatusAnterior={}, NovoStatus={}", numSolicitacao,
                    item.getId().getNumSolicitacaoItem(), statusAnterior,
                    SituationCreditRequestItems.RECARREGADO.getCode());
        }

        if (confirmados > 0) {
            consolidarStatusSolicitacao(numSolicitacao, codCanal, itens);
        }

        log.debug("Confirmação de recarga concluída para solicitação {}/{} - {} itens confirmados",
                numSolicitacao, codCanal, confirmados);
    }

    // Helper para converter domínio para EJpa (ajuste conforme seu projeto)
    private CreditRequestItemsEJpa toEntity(CreditRequestItems item) {
        // Se já tiver um mapper, use-o. Caso contrário, converta manualmente.
        CreditRequestItemsEJpa entity = new CreditRequestItemsEJpa();
        CreditRequestItemsKey key = item.getId();
        entity.setId(new CreditRequestItemsEJpaKey(
                key.getNumSolicitacao(), key.getNumSolicitacaoItem(), key.getCodCanal()
        ));
        entity.setCodCanal(item.getCodCanal());
        entity.setIdUsuarioCadastro(item.getIdUsuarioCadastro());
        entity.setCodVersao(item.getCodVersao());
        entity.setNumLogicoCartao(item.getNumLogicoCartao());
        entity.setCodProduto(item.getCodProduto());
        entity.setCodTipoDocumento(item.getCodTipoDocumento());
        entity.setCodSituacao(item.getCodSituacao());
        entity.setQtdItem(item.getQtdItem());
        entity.setVlUnitario(item.getVlUnitario());
        entity.setVlItem(item.getVlItem());
        entity.setDtRecarga(item.getDtRecarga());
        entity.setVlCarregado(item.getVlCarregado());
        entity.setVlAjuste(item.getVlAjuste());
        entity.setFlgAjuste(item.getFlgAjuste());
        entity.setIdFuncionario(item.getIdFuncionario());
        // entity.setCodAssinaturaHsm(item.getCodAssinaturaHsm());
        entity.setDtCadastro(item.getDtCadastro());
        entity.setDtManutencao(item.getDtManutencao());
        entity.setSeqRecarga(item.getSeqRecarga());
        entity.setDtEnvioHm(item.getDtEnvioHm());
        entity.setDtRetornoHm(item.getDtRetornoHm());
        entity.setIdUsuarioManutencao(item.getIdUsuarioManutencao());
        entity.setDtAssinatura(item.getDtAssinatura());
        entity.setDtPagtoEconomica(item.getDtPagtoEconomica());
        entity.setSqPid(item.getSqPid());
        entity.setDtInicProcesso(item.getDtInicProcesso());
        entity.setIdUsuarioCartao(item.getIdUsuarioCartao());
        entity.setSqRecarga(item.getSqRecarga());
        entity.setVlTxadm(item.getVlTxadm());
        entity.setVlTxserv(item.getVlTxserv());
        entity.setVlTxtotal(item.getVlTxtotal());
        entity.setFlgEvento(item.getFlgEvento());
        entity.setVlEvento(item.getVlEvento());
        entity.setFlgOutrasVias(item.getFlgOutrasVias());
        // entity.setCodAssdigRecarga(item.getCodAssdigRecarga());
        entity.setVlAutorizacaoHm(item.getVlAutorizacaoHm());
        entity.setFlgLiminarLoja(item.getFlgLiminarLoja());
        entity.setCodProdutoHm(item.getCodProdutoHm());
        entity.setQtdDiasUtilizados(item.getQtdDiasUtilizados());
        return entity;
    }

    private void consolidarStatusSolicitacao(Long numSolicitacao, String codCanal,
            List<CreditRequestItemsEJpa> itens) {

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
            historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, ORIGEM_TRANSICAO);

            log.info("Status da solicitação {}/{} consolidado - StatusAnterior={}, NovoStatus={}",
                    numSolicitacao, codCanal, statusAnterior, novoStatus);
        }
    }
}
