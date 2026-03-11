package br.sptrans.scd.creditrequest.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.port.out.jpa.entity.CreditRequestItemsEJpaKey;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsRepository;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.enums.ActionStatus;
import br.sptrans.scd.creditrequest.domain.enums.SearchMode;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;

import jakarta.transaction.Transactional;

@Service
public class CreditRequestService implements CreditRequestManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreditRequestService.class);

    /** Identificador de origem usado em {@code ID_ORIGEM_TRANSICAO} de todos os registros
     *  de histórico gerados por esta API. */
    static final String ORIGEM_TRANSICAO = "pedido_credito_scd";

    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestItemsRepository itemRepository;
    private final HistCreditRequestService historyService;
    private final TransitionSituationValidator transitionValidator;
    private final SituationAscertainedService situationAscertainedService;

    public CreditRequestService(
            CreditRequestRepository creditRequestRepository,
            CreditRequestItemsRepository itemRepository,
            HistCreditRequestService historyService) {
        this.creditRequestRepository = creditRequestRepository;
        this.itemRepository = itemRepository;
        this.historyService = historyService;
        this.transitionValidator = new TransitionSituationValidator();
        this.situationAscertainedService = new SituationAscertainedService();
    }

    // ── Ações de mudança de status ───────────────────────────────────

    @Override
    @Transactional
    public void block(BlockCommand comando) {
        log.info("Iniciando bloqueio - Entradas: {}", comando.itens().size());
        processarAlteracaoStatus(ActionStatus.BLOQUEAR, comando.itens(),
                null, null, null, null);
    }

    @Override
    @Transactional
    public void unblock(UnblockCommand comando) {
        log.info("Iniciando desbloqueio - Entradas: {}", comando.itens().size());
        processarAlteracaoStatus(ActionStatus.DESBLOQUEAR, comando.itens(),
                null, null, null, null);
    }

    @Override
    @Transactional
    public void cancel(CancelCommand comando) {
        log.info("Iniciando cancelamento - Entradas: {}", comando.itens().size());
        processarAlteracaoStatus(ActionStatus.CANCELAR, comando.itens(),
                null, null, null, null);
    }

    @Override
    @Transactional
    public void pay(PayCommand comando) {
        log.info("Iniciando pagamento - Itens: {}, FormaPagto: {}",
                comando.itens().size(), comando.codFormaPagto());

        validarAcaoPago(comando);

        List<OrderItemEntry> entries = convertPayItemEntries(comando.itens());
        processarAlteracaoStatus(ActionStatus.PAGO, entries,
                comando.codFormaPagto(), comando.vlPago(),
                comando.dtConfirmaPagto(), null);
    }

    @Override
    @Transactional
    public void acceptPendingSettlement(AcceptPendingCommand comando) {
        log.info("Iniciando aceite pendente liquidação - Entradas: {}", comando.itens().size());
        processarAlteracaoStatus(ActionStatus.ACEITO_PENDENTE_LIQUIDACAO, comando.itens(),
                null, null, null, comando.dtAceite());
    }

    // ── Consultas ────────────────────────────────────────────────────

    @Override
    public CreditRequest findById(String codTipoDocumento, Long idUsuarioCadastro) {
        return creditRequestRepository
                .findByCodTipoDocumentoAndIdUsuarioCadastro(codTipoDocumento, idUsuarioCadastro)
                .orElse(null);
    }

    @Override
    public CursorPage<CreditRequest> findAll(SearchCommand comando) {
        SearchMode mode = comando.searchMode() != null
                ? comando.searchMode() : SearchMode.OPERATIONAL;
        int limit = mode.getMaxPageSize() + 1;

        List<CreditRequest> results = creditRequestRepository.findWithCursor(
                comando.cursorNumSolicitacao(),
                comando.cursorCodCanal(),
                comando.codCanal(),
                comando.codSituacao(),
                comando.numLote(),
                comando.codFormaPagto(),
                comando.dtInicio(),
                comando.dtFim(),
                comando.dtLiberacaoEfetivaInicio(),
                comando.dtLiberacaoEfetivaFim(),
                comando.dtPagtoEconomicaInicio(),
                comando.dtPagtoEconomicaFim(),
                comando.dtFinanceiraInicio(),
                comando.dtFinanceiraFim(),
                comando.dtAlteracaoInicio(),
                comando.dtAlteracaoFim(),
                comando.vlTotalMin(),
                comando.vlTotalMax(),
                limit);

        boolean hasNext = results.size() > mode.getMaxPageSize();
        List<CreditRequest> content = hasNext
                ? results.subList(0, mode.getMaxPageSize()) : results;

        String nextCursorNumSol = null;
        String nextCursorCodCanal = null;
        if (hasNext && !content.isEmpty()) {
            CreditRequest last = content.get(content.size() - 1);
            nextCursorNumSol = last.getNumSolicitacao() != null
                    ? last.getNumSolicitacao().toString() : null;
            nextCursorCodCanal = last.getCodCanal();
        }

        return new CursorPage<>(content, content.size(), hasNext,
                nextCursorNumSol, nextCursorCodCanal);
    }

    // ── Core batch processing ────────────────────────────────────────

    private void processarAlteracaoStatus(
            ActionStatus acao,
            List<OrderItemEntry> entries,
            String codFormaPagto,
            BigDecimal vlPago,
            LocalDateTime dtConfirmaPagto,
            LocalDateTime dtAceite) {

        // Phase 1: Validate all transitions upfront
        for (OrderItemEntry entry : entries) {
            validarTransicoes(acao, entry);
        }

        int itensProcessados = 0;
        Set<String> solicitacoesParaConsolidar = new LinkedHashSet<>();
        List<CreditRequestItemsEJpa> itensParaRestaurar = new ArrayList<>();

        // Phase 2: Process each item
        for (OrderItemEntry entry : entries) {
            Long numSolicitacao = entry.numSolicitacao();
            String codCanal = entry.codCanal();

            for (Long numSolicitacaoItem : entry.numSolicitacaoItems()) {
                try {
                    CreditRequestItemsEJpaKey itemId = new CreditRequestItemsEJpaKey(
                            numSolicitacao, numSolicitacaoItem, codCanal);

                    Optional<CreditRequestItemsEJpa> itemOpt = itemRepository.findById(itemId);
                    if (itemOpt.isEmpty()) {
                        log.warn("Item não encontrado: Solicitação={}, Item={}, CodCanal={}",
                                numSolicitacao, numSolicitacaoItem, codCanal);
                        continue;
                    }

                    CreditRequestItemsEJpa item = itemOpt.get();
                    String statusAnterior = item.getCodSituacao();

                    if (acao == ActionStatus.DESBLOQUEAR) {
                        item.setCodSituacao(SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode());
                        itemRepository.save(item);
                        historyService.saveItemStatusHistory(toDomain(item), ORIGEM_TRANSICAO);
                        itensParaRestaurar.add(item);
                    } else {
                        String novoStatus = determinarNovoStatus(acao);
                        item.setCodSituacao(novoStatus);

                        if (acao == ActionStatus.PAGO) {
                            item.setDtPagtoEconomica(LocalDateTime.now());
                        }

                        itemRepository.save(item);
                        historyService.saveItemStatusHistory(toDomain(item), ORIGEM_TRANSICAO);

                        log.info("Item atualizado - Solicitação={}, Item={}, StatusAnterior={}, NovoStatus={}",
                                numSolicitacao, numSolicitacaoItem, statusAnterior, novoStatus);
                    }

                    solicitacoesParaConsolidar.add(numSolicitacao + ":" + codCanal);
                    itensProcessados++;
                } catch (Exception e) {
                    log.error("Erro ao processar item - Solicitação={}, Item={}",
                            numSolicitacao, numSolicitacaoItem, e);
                }
            }
        }

        // Phase 3: Consolidate solicitation statuses
        int solicitacoesAtualizadas = 0;
        for (String key : solicitacoesParaConsolidar) {
            String[] parts = key.split(":");
            Long numSolicitacao = Long.parseLong(parts[0]);
            String codCanal = parts[1];
            try {
                consolidarStatusSolicitacao(numSolicitacao, codCanal, acao,
                        codFormaPagto, vlPago, dtConfirmaPagto, dtAceite);
                solicitacoesAtualizadas++;
            } catch (Exception e) {
                log.error("Erro ao consolidar status da solicitação {}", numSolicitacao, e);
            }
        }

        // Phase 4: Restore DESBLOQUEAR items after consolidation
        if (acao == ActionStatus.DESBLOQUEAR) {
            for (CreditRequestItemsEJpa item : itensParaRestaurar) {
                try {
                    String statusAntesBloqueio = findStatusBeforeBloqueio(
                            item.getId().getNumSolicitacao(),
                            item.getId().getNumSolicitacaoItem(),
                            item.getId().getCodCanal());

                    if (statusAntesBloqueio != null) {
                        item.setCodSituacao(statusAntesBloqueio);
                        itemRepository.save(item);
                        log.info("Item desbloqueado e restaurado - Solicitação={}, Item={}, Status={}",
                                item.getId().getNumSolicitacao(),
                                item.getId().getNumSolicitacaoItem(),
                                statusAntesBloqueio);
                    } else {
                        log.warn("Status anterior ao bloqueio não encontrado - Solicitação={}, Item={}",
                                item.getId().getNumSolicitacao(),
                                item.getId().getNumSolicitacaoItem());
                    }
                } catch (Exception e) {
                    log.error("Erro ao restaurar item - Solicitação={}, Item={}",
                            item.getId().getNumSolicitacao(),
                            item.getId().getNumSolicitacaoItem(), e);
                }
            }
        }

        log.info("Alteração de status concluída - Itens processados: {}, Solicitações atualizadas: {}",
                itensProcessados, solicitacoesAtualizadas);
    }

    // ── Consolidação de status da solicitação ────────────────────────

    private void consolidarStatusSolicitacao(
            Long numSolicitacao, String codCanal, ActionStatus acao,
            String codFormaPagto, BigDecimal vlPago,
            LocalDateTime dtConfirmaPagto, LocalDateTime dtAceite) {

        List<CreditRequestItemsEJpa> itens = itemRepository.findById_NumSolicitacaoAndCodCanal(
                numSolicitacao, codCanal);
        if (itens.isEmpty()) {
            log.warn("Nenhum item encontrado para a solicitação {} e canal {}", numSolicitacao, codCanal);
            return;
        }

        Optional<CreditRequest> solicitacaoOpt = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal);
        if (solicitacaoOpt.isEmpty()) {
            log.warn("Solicitação {} não encontrada para canal {}", numSolicitacao, codCanal);
            return;
        }

        CreditRequest solicitacao = solicitacaoOpt.get();
        String statusAnterior = solicitacao.getCodSituacao();

        List<String> statusItens = itens.stream()
                .map(CreditRequestItemsEJpa::getCodSituacao)
                .filter(Objects::nonNull)
                .toList();

        String novoStatusSolicitacao = aplicarRegrasConsolidacao(statusItens);

        if (novoStatusSolicitacao != null && !novoStatusSolicitacao.equals(solicitacao.getCodSituacao())) {

            // Update flags based on action
            if (acao == ActionStatus.BLOQUEAR) {
                solicitacao.setFlgBloq("S");
                log.info("Setting FLG_BLOQ='S' para solicitação {} por ação BLOQUEAR", numSolicitacao);
            } else if (acao == ActionStatus.DESBLOQUEAR) {
                solicitacao.setFlgBloq("N");
                log.info("Setting FLG_BLOQ='N' para solicitação {} por ação DESBLOQUEAR", numSolicitacao);
            } else if (acao == ActionStatus.CANCELAR) {
                solicitacao.setFlgCanc("S");
                log.info("Setting FLG_CANC='S' para solicitação {} por ação CANCELAR", numSolicitacao);
            } else if (acao == ActionStatus.PAGO || acao == ActionStatus.ACEITO_PENDENTE_LIQUIDACAO) {
                // Apply transition-specific fields
                solicitacao.setCodSituacao(novoStatusSolicitacao);
                if (acao == ActionStatus.PAGO) {
                    solicitacao.setCodFormaPagto(codFormaPagto);
                    solicitacao.setDtConfirmaPagto(
                            dtConfirmaPagto != null ? dtConfirmaPagto : LocalDateTime.now());
                    solicitacao.setVlPago(vlPago != null ? vlPago : solicitacao.getVlTotal());
                } else {
                    solicitacao.setDtAceite(dtAceite != null ? dtAceite : LocalDateTime.now());
                }
                creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
                historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, ORIGEM_TRANSICAO);
                log.info("Status da solicitação {} consolidado - StatusAnterior: {}, NovoStatus: {}",
                        numSolicitacao, statusAnterior, novoStatusSolicitacao);
                return;
            }

            // Handle DESBLOQUEIO_SOLICITADO specially
            if (SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode().equals(novoStatusSolicitacao)) {
                solicitacao.setCodSituacao(novoStatusSolicitacao);
                creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
                historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, ORIGEM_TRANSICAO);

                String statusAntesBloqueio = findSolicitacaoStatusBeforeBloqueio(numSolicitacao, codCanal);
                if (statusAntesBloqueio != null) {
                    solicitacao.setCodSituacao(statusAntesBloqueio);
                    creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
                    log.info("Solicitação {} desbloqueada - Status restaurado={}", numSolicitacao, statusAntesBloqueio);
                } else {
                    log.warn("Status anterior ao bloqueio não encontrado para solicitação {}", numSolicitacao);
                }
            } else {
                solicitacao.setCodSituacao(novoStatusSolicitacao);
                creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
                historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, ORIGEM_TRANSICAO);
            }

            log.info("Status da solicitação {} consolidado - StatusAnterior: {}, NovoStatus: {}",
                    numSolicitacao, statusAnterior, novoStatusSolicitacao);
        } else if (novoStatusSolicitacao == null) {
            log.warn("Não foi possível determinar novo status para solicitação {} - Status atual: {}, Status itens: {}",
                    numSolicitacao, solicitacao.getCodSituacao(), statusItens);
        } else {
            log.debug("Status da solicitação {} permanece inalterado: {}", numSolicitacao, statusAnterior);
        }
    }

    private String aplicarRegrasConsolidacao(List<String> statusItens) {
        if (statusItens.isEmpty()) {
            log.warn("Lista de status de itens vazia para consolidação");
            return null;
        }

        String resultado = situationAscertainedService.apurarSituacaoPedido(statusItens);

        if (resultado == null) {
            // Legacy: todos DESBLOQUEIO_SOLICITADO
            boolean todosDesbloqueioSolicitado = statusItens.stream()
                    .allMatch(s -> SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode().equals(s));
            if (todosDesbloqueioSolicitado) {
                return SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode();
            }

            // Legacy: todos REJEITADO
            boolean todosRejeitado = statusItens.stream()
                    .allMatch(s -> SituationCreditRequestItems.REJEITADO.getCode().equals(s));
            if (todosRejeitado) {
                return SituationCreditRequest.REJEITADO.getCode();
            }

            // Legacy: mistura com sucesso → ATENDIDO_PARCIALMENTE
            boolean temSucesso = statusItens.stream().anyMatch(s ->
                    SituationCreditRequestItems.RECARREGADO.getCode().equals(s)
                    || SituationCreditRequestItems.PAGO.getCode().equals(s)
                    || SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode().equals(s));
            long statusUnicos = statusItens.stream().distinct().count();
            if (temSucesso && statusUnicos > 1) {
                return SituationCreditRequest.ATENDIDO_PARCIALMENTE.getCode();
            }
        }

        if (resultado == null) {
            log.debug("Nenhuma regra de consolidação se aplica aos status: {}", statusItens);
        }
        return resultado;
    }

    // ── Validações ───────────────────────────────────────────────────

    private void validarTransicoes(ActionStatus acao, OrderItemEntry entry) {
        for (Long numSolicitacaoItem : entry.numSolicitacaoItems()) {
            CreditRequestItemsEJpaKey itemId = new CreditRequestItemsEJpaKey(
                    entry.numSolicitacao(), numSolicitacaoItem, entry.codCanal());
            Optional<CreditRequestItemsEJpa> itemOpt = itemRepository.findById(itemId);
            if (itemOpt.isEmpty()) {
                continue;
            }
            transitionValidator.validarTransicaoItem(acao, itemOpt.get().getCodSituacao());
        }

        Optional<CreditRequest> solicitacaoOpt = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(entry.numSolicitacao(), entry.codCanal());
        if (solicitacaoOpt.isPresent()) {
            transitionValidator.validarTransicaoSolicitacao(acao, solicitacaoOpt.get().getCodSituacao());
        }
    }

    private void validarAcaoPago(PayCommand comando) {
        if (comando.codFormaPagto() == null || comando.codFormaPagto().trim().isEmpty()) {
            throw new IllegalArgumentException("COD_FORMA_PAGTO é obrigatório para ação PAGO");
        }

        if (comando.vlPago() != null) {
            if (comando.vlPago().compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Valor de Pagamento Inválido - VL_PAGO não pode ser zero");
            }

            Map<String, List<PayItemEntry>> grouped = comando.itens().stream()
                    .collect(Collectors.groupingBy(
                            i -> i.numSolicitacao() + ":" + i.codCanal()));

            for (Map.Entry<String, List<PayItemEntry>> entry : grouped.entrySet()) {
                String[] parts = entry.getKey().split(":");
                Long numSolicitacao = Long.parseLong(parts[0]);
                String codCanal = parts[1];

                List<CreditRequestItemsEJpa> itens = itemRepository
                        .findById_NumSolicitacaoAndCodCanal(numSolicitacao, codCanal);

                if (!itens.isEmpty()) {
                    BigDecimal totalCalculado = itens.stream()
                            .map(item -> {
                                BigDecimal vlItem = item.getVlItem() != null ? item.getVlItem() : BigDecimal.ZERO;
                                BigDecimal vlTxadm = item.getVlTxadm() != null ? item.getVlTxadm() : BigDecimal.ZERO;
                                BigDecimal vlTxserv = item.getVlTxserv() != null ? item.getVlTxserv() : BigDecimal.ZERO;
                                return vlItem.add(vlTxadm).add(vlTxserv);
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP);

                    BigDecimal vlPagoNorm = comando.vlPago().setScale(2, RoundingMode.HALF_UP);

                    if (vlPagoNorm.compareTo(totalCalculado) < 0) {
                        throw new IllegalArgumentException(
                                String.format("Valor de Pagamento Inválido - VL_PAGO (%s) menor que o total calculado (%s)",
                                        vlPagoNorm, totalCalculado));
                    }
                }
            }
        }
    }

    // ── Helpers de status ────────────────────────────────────────────

    private String determinarNovoStatus(ActionStatus acao) {
        return switch (acao) {
            case BLOQUEAR -> SituationCreditRequestItems.BLOQUEADO.getCode();
            case DESBLOQUEAR -> SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode();
            case CANCELAR -> SituationCreditRequestItems.CANCELADO.getCode();
            case PAGO -> SituationCreditRequestItems.PAGO.getCode();
            case ACEITO_PENDENTE_LIQUIDACAO -> SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode();
            case LIBERAR_RECARGA -> SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode();
        };
    }

    private String findStatusBeforeBloqueio(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        try {
            List<HistCreditRequestItems> history =
                    historyService.findItemStatusHistory(numSolicitacao, numSolicitacaoItem, codCanal);

            for (HistCreditRequestItems record : history) {
                String status = record.getCodSituacao();
                if (!SituationCreditRequestItems.BLOQUEADO.getCode().equals(status)
                        && !SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode().equals(status)
                        && !SituationCreditRequestItems.BLOQUEIO_SOLICITADO.getCode().equals(status)) {
                    return status;
                }
            }
        } catch (Exception e) {
            log.error("Erro ao buscar status anterior ao bloqueio - Solicitação={}, Item={}",
                    numSolicitacao, numSolicitacaoItem, e);
        }
        return null;
    }

    private String findSolicitacaoStatusBeforeBloqueio(Long numSolicitacao, String codCanal) {
        try {
            List<HistCreditRequest> history =
                    historyService.findRequestStatusHistory(numSolicitacao, codCanal);

            for (HistCreditRequest record : history) {
                String status = record.getCodSituacao();
                if (!SituationCreditRequestItems.BLOQUEADO.getCode().equals(status)
                        && !SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode().equals(status)
                        && !SituationCreditRequestItems.BLOQUEIO_SOLICITADO.getCode().equals(status)) {
                    return status;
                }
            }
        } catch (Exception e) {
            log.error("Erro ao buscar status anterior ao bloqueio da solicitação - Solicitação={}",
                    numSolicitacao, e);
        }
        return null;
    }

    // ── Conversão PayItemEntry → OrderItemEntry ──────────────────────

    private List<OrderItemEntry> convertPayItemEntries(List<PayItemEntry> payItems) {
        Map<String, List<Long>> grouped = new LinkedHashMap<>();
        for (PayItemEntry item : payItems) {
            String key = item.numSolicitacao() + ":" + item.codCanal();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(item.numSolicitacaoItem());
        }
        return grouped.entrySet().stream()
                .map(e -> {
                    String[] parts = e.getKey().split(":");
                    return new OrderItemEntry(Long.parseLong(parts[0]), parts[1], e.getValue());
                })
                .toList();
    }

    // ── Mapeamento EJpa → Domain ─────────────────────────────────────

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
