package br.sptrans.scd.creditrequest.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.application.port.in.ConfirmedRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;

import jakarta.transaction.Transactional;

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
public class ConfirmedRechargeService implements ConfirmedRechargeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmedRechargeService.class);
    private static final String ORIGEM_TRANSICAO = "confirmar_recarga_scd";

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestItemsRepository itemRepository;
    private final HistCreditRequestService historyService;
    private final SituationAscertainedService situationAscertainedService;

    public ConfirmedRechargeService(
            CreditRequestRepository creditRequestRepository,
            CreditRequestItemsRepository itemRepository,
            HistCreditRequestService historyService) {
        this.creditRequestRepository = creditRequestRepository;
        this.itemRepository = itemRepository;
        this.historyService = historyService;
        this.situationAscertainedService = new SituationAscertainedService();
    }

    @Override
    @Transactional
    public void confirmarRecarga(Long numSolicitacao, String codCanal) {
        log.debug("Confirmando recarga para solicitação {}/{}", numSolicitacao, codCanal);

        List<CreditRequestItemsEJpa> itens = itemRepository
                .findById_NumSolicitacaoAndCodCanal(numSolicitacao, codCanal);

        if (itens.isEmpty()) {
            log.warn("Nenhum item encontrado para solicitação {}/{}", numSolicitacao, codCanal);
            return;
        }

        int confirmados = 0;

        for (CreditRequestItemsEJpa item : itens) {
            if (!SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.getCode()
                    .equals(item.getCodSituacao())) {
                continue;
            }

            if (item.getDtRetornoHm() == null) {
                continue;
            }

            String statusAnterior = item.getCodSituacao();
            item.setCodSituacao(SituationCreditRequestItems.RECARREGADO.getCode());
            item.setDtManutencao(LocalDateTime.now());
            itemRepository.save(item);

            historyService.saveItemStatusHistory(toDomain(item), ORIGEM_TRANSICAO);
            confirmados++;

            log.info("Item confirmado - Solicitação={}, Item={}, StatusAnterior={}, NovoStatus={}",
                    numSolicitacao,
                    item.getId().getNumSolicitacaoItem(),
                    statusAnterior,
                    SituationCreditRequestItems.RECARREGADO.getCode());
        }

        if (confirmados > 0) {
            consolidarStatusSolicitacao(numSolicitacao, codCanal, itens);
        }

        log.debug("Confirmação de recarga concluída para solicitação {}/{} - {} itens confirmados",
                numSolicitacao, codCanal, confirmados);
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

    private CreditRequestItems toDomain(CreditRequestItemsEJpa e) {
        CreditRequestItems item = new CreditRequestItems();
        CreditRequestItemsKey key = new CreditRequestItemsKey();
        key.setNumSolicitacao(e.getId().getNumSolicitacao());
        key.setNumSolicitacaoItem(e.getId().getNumSolicitacaoItem());
        key.setCodCanal(e.getId().getCodCanal());
        item.setId(key);
        item.setCodCanal(e.getCodCanal());
        item.setIdUsuarioCadastro(e.getIdUsuarioCadastro());
        item.setCodVersao(e.getCodVersao());
        item.setNumLogicoCartao(e.getNumLogicoCartao());
        item.setCodProduto(e.getCodProduto());
        item.setCodTipoDocumento(e.getCodTipoDocumento());
        item.setCodSituacao(e.getCodSituacao());
        item.setQtdItem(e.getQtdItem());
        item.setVlUnitario(e.getVlUnitario());
        item.setVlItem(e.getVlItem());
        item.setDtRecarga(e.getDtRecarga());
        item.setVlCarregado(e.getVlCarregado());
        item.setVlAjuste(e.getVlAjuste());
        item.setFlgAjuste(e.getFlgAjuste());
        item.setIdFuncionario(e.getIdFuncionario());
        item.setCodAssinaturaHsm(e.getCodAssinaturaHsm());
        item.setDtCadastro(e.getDtCadastro());
        item.setDtManutencao(e.getDtManutencao());
        item.setSeqRecarga(e.getSeqRecarga());
        item.setDtEnvioHm(e.getDtEnvioHm());
        item.setDtRetornoHm(e.getDtRetornoHm());
        item.setIdUsuarioManutencao(e.getIdUsuarioManutencao());
        item.setDtAssinatura(e.getDtAssinatura());
        item.setDtPagtoEconomica(e.getDtPagtoEconomica());
        item.setSqPid(e.getSqPid());
        item.setDtInicProcesso(e.getDtInicProcesso());
        item.setIdUsuarioCartao(e.getIdUsuarioCartao());
        item.setSqRecarga(e.getSqRecarga());
        item.setVlTxadm(e.getVlTxadm());
        item.setVlTxserv(e.getVlTxserv());
        item.setVlTxtotal(e.getVlTxtotal());
        item.setFlgEvento(e.getFlgEvento());
        item.setVlEvento(e.getVlEvento());
        item.setFlgOutrasVias(e.getFlgOutrasVias());
        item.setCodAssdigRecarga(e.getCodAssdigRecarga());
        item.setVlAutorizacaoHm(e.getVlAutorizacaoHm());
        item.setFlgLiminarLoja(e.getFlgLiminarLoja());
        item.setCodProdutoHm(e.getCodProdutoHm());
        item.setQtdDiasUtilizados(e.getQtdDiasUtilizados());
        item.setCodMidia(e.getCodMidia());
        return item;
    }
}
