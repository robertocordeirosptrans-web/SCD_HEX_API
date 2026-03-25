package br.sptrans.scd.creditrequest.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.application.port.in.ProcessRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import jakarta.transaction.Transactional;

/**
 * Implementação do use case de processamento de recarga.
 *
 * <p>Transição do pedido: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06).
 * Transição dos itens: LIBERADO_PARA_RECARGA(05) → EM_PROCESSO_DE_RECARGA(06).</p>
 *
 * <p>Itens cujo valor efetivo (vlItem + vlEvento) é ≤ 0 são marcados
 * diretamente como RECARREGADO(07) com valor zero.</p>
 */
@Service
public class ProcessRechargeService implements ProcessRechargeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessRechargeService.class);
    private static final String ORIGEM_TRANSICAO = "processar_recarga_scd";

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestItemsRepository itemRepository;
    private final HistCreditRequestService historyService;
    private final SituationAscertainedService situationAscertainedService;

    public ProcessRechargeService(
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
    public void processarRecarga(ProcessRechargeCommand comando) {
        CreditRequest solicitacao = creditRequestRepository
                .findByCodTipoDocumentoAndIdUsuarioCadastro(
                        comando.codTipoDocumento(), comando.idUsuarioCadastro())
                .orElse(null);

        if (solicitacao == null) {
            log.warn("Solicitação não encontrada para codTipoDocumento={}, idUsuarioCadastro={}",
                    comando.codTipoDocumento(), comando.idUsuarioCadastro());
            return;
        }

        Long numSolicitacao = solicitacao.getNumSolicitacao();
        String codCanal = solicitacao.getCodCanal();

        log.debug("Processando recarga para solicitação {}/{}", numSolicitacao, codCanal);

        List<CreditRequestItemsEJpa> itens = itemRepository
            .findById_NumSolicitacaoAndCodCanal(numSolicitacao, codCanal);

        if (itens.isEmpty()) {
            log.warn("Nenhum item encontrado para solicitação {}/{}", numSolicitacao, codCanal);
            return;
        }

        int processados = 0;

        for (CreditRequestItemsEJpa item : itens) {
            if (!SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode()
                    .equals(item.getCodSituacao())) {
                continue;
            }

            BigDecimal vlEvento = item.getVlEvento();
            BigDecimal vlItem = item.getVlItem() != null ? item.getVlItem() : BigDecimal.ZERO;
            BigDecimal valorEfetivo = vlItem.add(vlEvento != null ? vlEvento : BigDecimal.ZERO);

            if (valorEfetivo.compareTo(BigDecimal.ZERO) <= 0) {
                // Item com valor zero → marca diretamente como RECARREGADO
                item.setCodSituacao(SituationCreditRequestItems.RECARREGADO.getCode());
                item.setVlCarregado(BigDecimal.ZERO);
                item.setDtRetornoHm(LocalDateTime.now());
            } else {
                item.setCodSituacao(SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.getCode());
                item.setDtEnvioHm(LocalDateTime.now());
            }

            if (comando.sqPid() != null) {
                item.setSqPid(comando.sqPid());
                item.setDtInicProcesso(LocalDateTime.now());
            }

            item.setDtManutencao(LocalDateTime.now());
            itemRepository.save(item);
            historyService.saveItemStatusHistory(toDomain(item), ORIGEM_TRANSICAO);
            processados++;

            log.info("Item processado - Solicitação={}, Item={}, NovoStatus={}",
                    numSolicitacao, item.getId().getNumSolicitacaoItem(), item.getCodSituacao());
        }

        if (processados > 0) {
            consolidarStatusSolicitacao(numSolicitacao, codCanal, itens);
        }

        log.debug("Processamento de recarga concluído para solicitação {}/{} - {} itens processados",
                numSolicitacao, codCanal, processados);
    }

    @Override
    @Transactional
    public void processarItemRecarga(ProcessItemCommand comando) {
        CreditRequest solicitacao = creditRequestRepository
                .findByCodTipoDocumentoAndIdUsuarioCadastro(
                        comando.codTipoDocumento(), comando.idUsuarioCadastro())
                .orElse(null);

        if (solicitacao == null) {
            log.warn("Solicitação não encontrada para codTipoDocumento={}, idUsuarioCadastro={}",
                    comando.codTipoDocumento(), comando.idUsuarioCadastro());
            return;
        }

        Long numSolicitacao = solicitacao.getNumSolicitacao();
        String codCanal = solicitacao.getCodCanal();
        Long numSolicitacaoItem = comando.seqItem() != null ? comando.seqItem().longValue() : null;

        if (numSolicitacaoItem == null) {
            log.warn("seqItem é obrigatório para processarItemRecarga");
            return;
        }

        CreditRequestItemsEJpaKey itemId = new CreditRequestItemsEJpaKey(
                numSolicitacao, numSolicitacaoItem, codCanal);

        Optional<CreditRequestItemsEJpa> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            log.warn("Item não encontrado: Solicitação={}, Item={}, CodCanal={}",
                    numSolicitacao, numSolicitacaoItem, codCanal);
            return;
        }

        CreditRequestItemsEJpa item = itemOpt.get();

        if (!SituationCreditRequestItems.EM_PROCESSO_DE_RECARGA.getCode()
                .equals(item.getCodSituacao())) {
            log.warn("Item {}/{}/{} não está em EM_PROCESSO_DE_RECARGA, status atual={}",
                    numSolicitacao, numSolicitacaoItem, codCanal, item.getCodSituacao());
            return;
        }

        item.setCodSituacao(SituationCreditRequestItems.RECARREGADO.getCode());
        if (comando.vlCarregado() != null) {
            item.setVlCarregado(comando.vlCarregado());
        }
        if (comando.codAssinaturaHsm() != null) {
            item.setCodAssinaturaHsm(comando.codAssinaturaHsm());
        }
        item.setDtRetornoHm(LocalDateTime.now());
        item.setDtManutencao(LocalDateTime.now());

        itemRepository.save(item);
        historyService.saveItemStatusHistory(toDomain(item), ORIGEM_TRANSICAO);

        log.info("Item recarga processado - Solicitação={}, Item={}, NovoStatus=RECARREGADO",
                numSolicitacao, numSolicitacaoItem);

        List<CreditRequestItemsEJpa> todosItens = itemRepository
            .findById_NumSolicitacaoAndCodCanal(numSolicitacao, codCanal);
        consolidarStatusSolicitacao(numSolicitacao, codCanal, todosItens);
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
        return item;
    }
}
