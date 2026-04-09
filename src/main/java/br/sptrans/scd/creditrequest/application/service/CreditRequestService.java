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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.application.usecases.CreateCreditRequestCase;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.enums.ActionStatus;
import br.sptrans.scd.creditrequest.domain.enums.SearchMode;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import br.sptrans.scd.shared.cache.InvalidateOrderCache;
import br.sptrans.scd.shared.helper.StatusConsolidationHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditRequestService implements CreditRequestManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreditRequestService.class);

    /**
     * Identificador de origem usado em {@code ID_ORIGEM_TRANSICAO} de todos os
     * registros de histórico gerados por esta API.
     */
    static final String ORIGEM_TRANSICAO = "pedido_credito_scd";

    private final CreditRequestPort creditRequestRepository;
    private final CreditRequestItemsPort itemRepository;
    private final CreditRequestMapper creditRequestMapper;
    private final HistCreditRequestService historyService;
    private final TransitionSituationValidator transitionValidator;
    private final SituationAscertainedService situationAscertainedService;
    private final StatusConsolidationHelper statusConsolidationHelper;

    private final CreateCreditRequestCase createCreditRequestCase;

    // ── Ações de mudança de status ───────────────────────────────────
    @Override
    @Transactional
    @InvalidateOrderCache
    public void block(BlockCommand comando) {
        log.info("Iniciando bloqueio - Entradas: {}", comando.itens().size());
        processarAlteracaoStatus(ActionStatus.BLOQUEAR, comando.itens(),
                null, null, null, null);
    }

    @Override
    @Transactional
    @InvalidateOrderCache
    public void unblock(UnblockCommand comando) {
        log.info("Iniciando desbloqueio - Entradas: {}", comando.itens().size());
        processarAlteracaoStatus(ActionStatus.DESBLOQUEAR, comando.itens(),
                null, null, null, null);
    }

    @Override
    @Transactional
    @InvalidateOrderCache
    public void cancel(CancelCommand comando) {
        log.info("Iniciando cancelamento - Entradas: {}", comando.itens().size());
        processarAlteracaoStatus(ActionStatus.CANCELAR, comando.itens(),
                null, null, null, null);
    }

    @Override
    @Transactional
    @InvalidateOrderCache
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
    @InvalidateOrderCache
    public void acceptPendingSettlement(AcceptPendingCommand comando) {
        log.info("Iniciando aceite pendente liquidação - Entradas: {}", comando.itens().size());
        processarAlteracaoStatus(ActionStatus.ACEITO_PENDENTE_LIQUIDACAO, comando.itens(),
                null, null, null, comando.dtAceite());
    }

    /**
     * Cria um novo pedido de crédito em lote, processando cada item e
     * retornando o resultado detalhado.
     *
     * @param request dados do pedido e itens
     * @return resposta com itens processados e rejeitados
     */
    @Override
    @Transactional
    @InvalidateOrderCache
    public CreateRequestResponse createCreditRequest(CreateRequestCredit request, String idempotencyKey, Long userId) {
        return createCreditRequestCase.execute(request, idempotencyKey, userId);
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
                ? comando.searchMode()
                : SearchMode.OPERATIONAL;

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
                ? results.subList(0, mode.getMaxPageSize())
                : results;

        String nextCursorNumSol = null;
        String nextCursorCodCanal = null;
        if (hasNext && !content.isEmpty()) {
            CreditRequest last = content.get(content.size() - 1);
            nextCursorNumSol = last.getNumSolicitacao() != null
                    ? last.getNumSolicitacao().toString()
                    : null;
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
                    CreditRequestItemsKey itemId = new CreditRequestItemsKey();
                    itemId.setNumSolicitacao(numSolicitacao);
                    itemId.setNumSolicitacaoItem(numSolicitacaoItem);
                    itemId.setCodCanal(codCanal);

                    Optional<CreditRequestItems> itemOpt = itemRepository.findById(itemId);
                    if (itemOpt.isEmpty()) {
                        log.warn("Item não encontrado: Solicitação={}, Item={}, CodCanal={}",
                                numSolicitacao, numSolicitacaoItem, codCanal);
                        continue;
                    }

                    CreditRequestItems item = itemOpt.get();
                    SituationCreditRequestItems statusAnterior = item.getCodSituacao();

                    if (acao == ActionStatus.DESBLOQUEAR) {
                        item.setCodSituacao(SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO);
                        itemRepository.save(item);
                        historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO);
                        itensParaRestaurar.add(creditRequestMapper.toEntityItem(item));
                    } else {
                        SituationCreditRequestItems novoStatus = determinarNovoStatus(acao);
                        item.setCodSituacao(novoStatus);

                        if (acao == ActionStatus.PAGO) {
                            log.warn("Item encontrado: NumLogicoCartao={}, CodCanal={}",
                                    item.getNumLogicoCartao(), codCanal);
                            item.setDtPagtoEconomica(dtConfirmaPagto != null ? dtConfirmaPagto : LocalDateTime.now());
                        }

                        itemRepository.save(item);
                        historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO);

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
            Long numSolicitacao = Long.valueOf(parts[0]);
            String codCanal = parts[1];
            try {
                // Atualizar campos do pedido durante o pagamento
                if (acao == ActionStatus.PAGO) {
                    Optional<CreditRequest> pedidoOpt = creditRequestRepository
                            .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal);
                    if (pedidoOpt.isPresent()) {
                        CreditRequest pedido = pedidoOpt.get();
                        // Atualiza os campos
                        pedido.setDtPagtoEconomica(dtConfirmaPagto != null ? dtConfirmaPagto : LocalDateTime.now());
                        pedido.setDtConfirmaPagto(dtConfirmaPagto);
                        pedido.setCodFormaPagto(codFormaPagto);
                        creditRequestRepository.save(pedido);
                        // Registra no histórico da solicitação
                        historyService.saveRequestStatusHistory(pedido, numSolicitacao, codCanal, ORIGEM_TRANSICAO);
                    }
                }
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
                        // Converter para domínio antes de salvar
                        itemRepository.save(creditRequestMapper.toDomainItem(item));
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

        // Buscar todos os itens de uma vez só, evitando N+1 queries e gaps
        List<CreditRequestItems> itensDomain = itemRepository.findAllBySolicitacao(numSolicitacao, codCanal);
        if (itensDomain.isEmpty()) {
            log.warn("Nenhum item encontrado para a solicitação {} e canal {}", numSolicitacao, codCanal);
            return;
        }
        // Se necessário, converte para entidade JPA (mantendo compatibilidade com o
        // restante do método)
        List<CreditRequestItemsEJpa> itens = itensDomain.stream()
                .map(creditRequestMapper::toEntityItem)
                .toList();

        Optional<CreditRequest> solicitacaoOpt = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal);
        if (solicitacaoOpt.isEmpty()) {
            log.warn("Solicitação {} não encontrada para canal {}", numSolicitacao, codCanal);
            return;
        }

        CreditRequest solicitacao = solicitacaoOpt.get();
        SituationCreditRequest statusAnterior = solicitacao.getCodSituacao();

        List<String> statusItens = itens.stream()
                .map(CreditRequestItemsEJpa::getCodSituacao)
                .filter(Objects::nonNull)
                .toList();

        String novoStatusSolicitacao = statusConsolidationHelper.apurarNovoStatus(itens, solicitacao.getCodSituacao().getCode());

        if (novoStatusSolicitacao != null && !novoStatusSolicitacao.equals(solicitacao.getCodSituacao().getCode())) {

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
                creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
                historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, ORIGEM_TRANSICAO);
                log.info("Histórico de cancelamento registrado para solicitação {}", numSolicitacao);
                return;
            } else if (acao == ActionStatus.PAGO || acao == ActionStatus.ACEITO_PENDENTE_LIQUIDACAO) {
                // Apply transition-specific fields
                solicitacao.setCodSituacao(SituationCreditRequest.fromCode(novoStatusSolicitacao));
                if (acao == ActionStatus.PAGO) {
                    solicitacao.setCodFormaPagto(codFormaPagto);
                    solicitacao.setDtConfirmaPagto(LocalDateTime.now());
                    solicitacao.setDtPagtoEconomica(LocalDateTime.now());
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
                solicitacao.setCodSituacao(SituationCreditRequest.fromCode(novoStatusSolicitacao));
                creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
                historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, ORIGEM_TRANSICAO);

                String statusAntesBloqueio = findSolicitacaoStatusBeforeBloqueio(numSolicitacao, codCanal);
                if (statusAntesBloqueio != null) {
                    solicitacao.setCodSituacao(SituationCreditRequest.fromCode(statusAntesBloqueio));
                    creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
                    log.info("Solicitação {} desbloqueada - Status restaurado={}", numSolicitacao, statusAntesBloqueio);
                } else {
                    log.warn("Status anterior ao bloqueio não encontrado para solicitação {}", numSolicitacao);
                }
            } else {
                solicitacao.setCodSituacao(SituationCreditRequest.fromCode(novoStatusSolicitacao));
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

    // ── Validações de Transações
    // ───────────────────────────────────────────────────
    private void validarTransicoes(ActionStatus acao, OrderItemEntry entry) {
        // 1. Buscar todos os itens de uma única vez
        List<CreditRequestItemsKey> itemIds = entry.numSolicitacaoItems().stream()
                .map(numSolicitacaoItem -> {
                    CreditRequestItemsKey itemId = new CreditRequestItemsKey();
                    itemId.setNumSolicitacao(entry.numSolicitacao());
                    itemId.setNumSolicitacaoItem(numSolicitacaoItem);
                    itemId.setCodCanal(entry.codCanal());
                    return itemId;
                })
                .collect(Collectors.toList());

        // 2. Query única para buscar todos os itens (IN clause)
        List<CreditRequestItems> itens = itemRepository.findAllById(itemIds);

        // 3. Criar mapa para acesso O(1) por ID
        Map<CreditRequestItemsKey, CreditRequestItems> itensMap = itens.stream()
                .collect(Collectors.toMap(
                        CreditRequestItems::getId,
                        Function.identity()));

        // 4. Validar apenas os itens que existem
        for (Long numSolicitacaoItem : entry.numSolicitacaoItems()) {
            CreditRequestItemsKey itemId = new CreditRequestItemsKey();
            itemId.setNumSolicitacao(entry.numSolicitacao());
            itemId.setNumSolicitacaoItem(numSolicitacaoItem);
            itemId.setCodCanal(entry.codCanal());

            CreditRequestItems item = itensMap.get(itemId);
            if (item != null) {
                transitionValidator.validarTransicaoItem(acao, item.getCodSituacao().getCode());
            }
        }

        // 5. Buscar solicitação (mantém-se uma query)
        Optional<CreditRequest> solicitacaoOpt = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(entry.numSolicitacao(), entry.codCanal());
        if (solicitacaoOpt.isPresent()) {
            transitionValidator.validarTransicaoSolicitacao(acao, solicitacaoOpt.get().getCodSituacao().getCode());
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

            // 1. Coletar TODAS as chaves de uma vez
            List<CreditRequestItemsKey> todasChaves = comando.itens().stream()
                    .map(payItem -> {
                        CreditRequestItemsKey key = new CreditRequestItemsKey();
                        key.setNumSolicitacao(payItem.numSolicitacao());
                        key.setNumSolicitacaoItem(payItem.numSolicitacaoItem());
                        key.setCodCanal(payItem.codCanal());
                        return key;
                    })
                    .collect(Collectors.toList());

            // 2. Buscar TODOS os itens em UMA query
            Map<CreditRequestItemsKey, CreditRequestItems> itensMap = itemRepository.findAllById(todasChaves)
                    .stream()
                    .collect(Collectors.toMap(
                            CreditRequestItems::getId,
                            Function.identity()));

            // 3. Agrupar por solicitação/canal para validação
            Map<String, List<PayItemEntry>> grouped = comando.itens().stream()
                    .collect(Collectors.groupingBy(
                            i -> i.numSolicitacao() + ":" + i.codCanal()));

            for (Map.Entry<String, List<PayItemEntry>> entry : grouped.entrySet()) {
                String[] parts = entry.getKey().split(":");
                Long numSolicitacao = Long.valueOf(parts[0]);
                String codCanal = parts[1];

                // 4. Filtrar itens existentes deste grupo
                List<CreditRequestItems> domainItens = entry.getValue().stream()
                        .map(payItem -> {
                            CreditRequestItemsKey key = new CreditRequestItemsKey();
                            key.setNumSolicitacao(numSolicitacao);
                            key.setNumSolicitacaoItem(payItem.numSolicitacaoItem());
                            key.setCodCanal(codCanal);
                            return itensMap.get(key);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (!domainItens.isEmpty()) {
                    List<CreditRequestItemsEJpa> itens = domainItens.stream()
                            .map(creditRequestMapper::toEntityItem)
                            .collect(Collectors.toList());

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
                                String.format(
                                        "Valor de Pagamento Inválido - VL_PAGO (%s) menor que o total calculado (%s)",
                                        vlPagoNorm, totalCalculado));
                    }
                }
            }
        }
    }

    // ── Helpers de status ────────────────────────────────────────────
    private SituationCreditRequestItems determinarNovoStatus(ActionStatus acao) {
        return switch (acao) {
            case BLOQUEAR -> SituationCreditRequestItems.BLOQUEADO;
            case DESBLOQUEAR -> SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO;
            case CANCELAR -> SituationCreditRequestItems.CANCELADO;
            case PAGO -> SituationCreditRequestItems.PAGO;
            case ACEITO_PENDENTE_LIQUIDACAO -> SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO;
            case LIBERAR_RECARGA -> SituationCreditRequestItems.LIBERADO_PARA_RECARGA;
        };
    }

    private String findStatusBeforeBloqueio(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        try {
            List<HistCreditRequestItems> history = historyService.findItemStatusHistory(numSolicitacao,
                    numSolicitacaoItem, codCanal);

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
            List<HistCreditRequest> history = historyService.findRequestStatusHistory(numSolicitacao, codCanal);

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
                    return new OrderItemEntry(Long.valueOf(parts[0]), parts[1], e.getValue());
                })
                .toList();
    }

}
