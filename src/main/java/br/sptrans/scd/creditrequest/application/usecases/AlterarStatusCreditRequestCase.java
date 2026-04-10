package br.sptrans.scd.creditrequest.application.usecases;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.stereotype.Component;

import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEntity;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.OrderItemEntry;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.PayCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.PayItemEntry;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.application.service.HistCreditRequestService;
import br.sptrans.scd.creditrequest.application.service.TransitionSituationValidator;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
import br.sptrans.scd.creditrequest.domain.HistCreditRequest;
import br.sptrans.scd.creditrequest.domain.HistCreditRequestItems;
import br.sptrans.scd.creditrequest.domain.enums.ActionStatus;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequest;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import br.sptrans.scd.shared.helper.StatusConsolidationHelper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlterarStatusCreditRequestCase {

    private static final Logger log = LoggerFactory.getLogger(AlterarStatusCreditRequestCase.class);

    static final String ORIGEM_TRANSICAO = "pedido_credito_scd";

    private final CreditRequestPort creditRequestRepository;
    private final CreditRequestItemsPort itemRepository;
    private final CreditRequestMapper creditRequestMapper;
    private final HistCreditRequestService historyService;
    private final TransitionSituationValidator transitionValidator;
    private final StatusConsolidationHelper statusConsolidationHelper;

    // ── Entry points ──────────────────────────────────────────────────

    public void execute(
            ActionStatus acao,
            List<OrderItemEntry> entries,
            String codFormaPagto,
            BigDecimal vlPago,
            LocalDateTime dtConfirmaPagto,
            LocalDateTime dtAceite) {

        // Phase 1: Batch load + validate. Returns the items map so Phase 2 reuses it,
        // eliminating the N+1 that existed when findById was called one-by-one.
        Map<CreditRequestItemsKey, CreditRequestItems> allItemsMap = new HashMap<>();
        for (OrderItemEntry entry : entries) {
            allItemsMap.putAll(carregarEValidar(acao, entry));
        }

        int itensProcessados = 0;
        Set<String> solicitacoesParaConsolidar = new LinkedHashSet<>();
        List<CreditRequestItemsEntity> itensParaRestaurar = new ArrayList<>();

        // Phase 2: Process each item using the already-loaded map (zero extra queries)
        for (OrderItemEntry entry : entries) {
            Long numSolicitacao = entry.numSolicitacao();
            String codCanal = entry.codCanal();

            for (Long numSolicitacaoItem : entry.numSolicitacaoItems()) {
                CreditRequestItemsKey itemId = new CreditRequestItemsKey();
                itemId.setNumSolicitacao(numSolicitacao);
                itemId.setNumSolicitacaoItem(numSolicitacaoItem);
                itemId.setCodCanal(codCanal);

                CreditRequestItems item = allItemsMap.get(itemId);
                if (item == null) {
                    log.warn("Item não encontrado: Solicitação={}, Item={}, CodCanal={}",
                            numSolicitacao, numSolicitacaoItem, codCanal);
                    throw new ResourceNotFoundException(
                        "Pedido de Crédito",
                        "numSolicitacao/numSolicitacaoItem",
                        numSolicitacao + "/" + numSolicitacaoItem
                    );
                }

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
                        item.setDtPagtoEconomica(dtConfirmaPagto != null ? dtConfirmaPagto : LocalDateTime.now());
                    }

                    itemRepository.save(item);
                    historyService.saveItemStatusHistoryBatch(List.of(item), ORIGEM_TRANSICAO);

                    log.info("Item atualizado - Solicitação={}, Item={}, StatusAnterior={}, NovoStatus={}",
                            numSolicitacao, numSolicitacaoItem, statusAnterior, novoStatus);
                }

                solicitacoesParaConsolidar.add(numSolicitacao + ":" + codCanal);
                itensProcessados++;
            }
        }

        // Phase 3: Consolidate solicitation statuses.
        // For PAGO: load the pedido once, update its fields, then pass it into
        // consolidarStatusSolicitacao to avoid a second SELECT on the same row.
        int solicitacoesAtualizadas = 0;
        for (String key : solicitacoesParaConsolidar) {
            String[] parts = key.split(":");
            Long numSolicitacao = Long.valueOf(parts[0]);
            String codCanal = parts[1];
            try {
                CreditRequest pedidoPreloaded = null;
                if (acao == ActionStatus.PAGO) {
                    Optional<CreditRequest> pedidoOpt = creditRequestRepository
                            .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal);
                    if (pedidoOpt.isPresent()) {
                        CreditRequest pedido = pedidoOpt.get();
                        pedido.setDtPagtoEconomica(dtConfirmaPagto != null ? dtConfirmaPagto : LocalDateTime.now());
                        pedido.setDtConfirmaPagto(dtConfirmaPagto);
                        pedido.setCodFormaPagto(codFormaPagto);
                        creditRequestRepository.save(pedido);
                        historyService.saveRequestStatusHistory(pedido, numSolicitacao, codCanal, ORIGEM_TRANSICAO);
                        pedidoPreloaded = pedido;
                    }
                }
                consolidarStatusSolicitacao(numSolicitacao, codCanal, acao,
                        codFormaPagto, vlPago, dtConfirmaPagto, dtAceite, pedidoPreloaded);
                solicitacoesAtualizadas++;
            } catch (Exception e) {
                log.error("Erro ao consolidar status da solicitação {}", numSolicitacao, e);
            }
        }

        // Phase 4: Restore DESBLOQUEAR items to their pre-block status
        if (acao == ActionStatus.DESBLOQUEAR) {
            for (CreditRequestItemsEntity item : itensParaRestaurar) {
                try {
                    String statusAntesBloqueio = findStatusBeforeBloqueio(
                            item.getId().getNumSolicitacao(),
                            item.getId().getNumSolicitacaoItem(),
                            item.getId().getCodCanal());

                    if (statusAntesBloqueio != null) {
                        item.setCodSituacao(statusAntesBloqueio);
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

    public void executePay(PayCommand comando) {
        validarAcaoPago(comando);
        List<OrderItemEntry> entries = convertPayItemEntries(comando.itens());
        execute(ActionStatus.PAGO, entries,
                comando.codFormaPagto(), comando.vlPago(),
                comando.dtConfirmaPagto(), null);
    }

    // ── Phase 1: batch load + validate ───────────────────────────────
    // Returns the items map so Phase 2 can reuse it without re-querying.

    private Map<CreditRequestItemsKey, CreditRequestItems> carregarEValidar(ActionStatus acao, OrderItemEntry entry) {
        List<CreditRequestItemsKey> itemIds = entry.numSolicitacaoItems().stream()
                .map(numSolicitacaoItem -> {
                    CreditRequestItemsKey itemId = new CreditRequestItemsKey();
                    itemId.setNumSolicitacao(entry.numSolicitacao());
                    itemId.setNumSolicitacaoItem(numSolicitacaoItem);
                    itemId.setCodCanal(entry.codCanal());
                    return itemId;
                })
                .collect(Collectors.toList());

        List<CreditRequestItems> itens = itemRepository.findAllById(itemIds);

        Map<CreditRequestItemsKey, CreditRequestItems> itensMap = itens.stream()
                .collect(Collectors.toMap(
                        CreditRequestItems::getId,
                        Function.identity()));

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

        Optional<CreditRequest> solicitacaoOpt = creditRequestRepository
                .findByNumSolicitacaoAndCodCanal(entry.numSolicitacao(), entry.codCanal());
        if (solicitacaoOpt.isPresent()) {
            transitionValidator.validarTransicaoSolicitacao(acao, solicitacaoOpt.get().getCodSituacao().getCode());
        }

        return itensMap;
    }

    // ── Consolidação de status da solicitação ─────────────────────────
    // pedidoPreloaded: quando não-null (caso PAGO), o registro já foi carregado
    // e salvo na Phase 3, evitando um SELECT extra aqui.

    private void consolidarStatusSolicitacao(
            Long numSolicitacao, String codCanal, ActionStatus acao,
            String codFormaPagto, BigDecimal vlPago,
            LocalDateTime dtConfirmaPagto, LocalDateTime dtAceite,
            CreditRequest pedidoPreloaded) {

        List<CreditRequestItems> itens = itemRepository.findAllBySolicitacao(numSolicitacao, codCanal);
        if (itens.isEmpty()) {
            log.warn("Nenhum item encontrado para a solicitação {} e canal {}", numSolicitacao, codCanal);
            return;
        }

        CreditRequest solicitacao;
        if (pedidoPreloaded != null) {
            solicitacao = pedidoPreloaded;
        } else {
            Optional<CreditRequest> solicitacaoOpt = creditRequestRepository
                    .findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal);
            if (solicitacaoOpt.isEmpty()) {
                log.warn("Solicitação {} não encontrada para canal {}", numSolicitacao, codCanal);
                return;
            }
            solicitacao = solicitacaoOpt.get();
        }

        SituationCreditRequest statusAnterior = solicitacao.getCodSituacao();

        List<SituationCreditRequestItems> statusItens = itens.stream()
                .map(CreditRequestItems::getCodSituacao)
                .filter(Objects::nonNull)
                .toList();

        String novoStatusSolicitacao = statusConsolidationHelper.apurarNovoStatus(itens, solicitacao.getCodSituacao().getCode());

        if (novoStatusSolicitacao != null && !novoStatusSolicitacao.equals(solicitacao.getCodSituacao().getCode())) {

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

    // ── Validação PAGO ────────────────────────────────────────────────

    private void validarAcaoPago(PayCommand comando) {
        if (comando.codFormaPagto() == null || comando.codFormaPagto().trim().isEmpty()) {
            throw new IllegalArgumentException("COD_FORMA_PAGTO é obrigatório para ação PAGO");
        }

        if (comando.vlPago() != null) {
            if (comando.vlPago().compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Valor de Pagamento Inválido - VL_PAGO não pode ser zero");
            }

            List<CreditRequestItemsKey> todasChaves = comando.itens().stream()
                    .map(payItem -> {
                        CreditRequestItemsKey key = new CreditRequestItemsKey();
                        key.setNumSolicitacao(payItem.numSolicitacao());
                        key.setNumSolicitacaoItem(payItem.numSolicitacaoItem());
                        key.setCodCanal(payItem.codCanal());
                        return key;
                    })
                    .collect(Collectors.toList());

            Map<CreditRequestItemsKey, CreditRequestItems> itensMap = itemRepository.findAllById(todasChaves)
                    .stream()
                    .collect(Collectors.toMap(
                            CreditRequestItems::getId,
                            Function.identity()));

            Map<String, List<PayItemEntry>> grouped = comando.itens().stream()
                    .collect(Collectors.groupingBy(i -> i.numSolicitacao() + ":" + i.codCanal()));

            for (Map.Entry<String, List<PayItemEntry>> entry : grouped.entrySet()) {
                String[] parts = entry.getKey().split(":");
                Long numSolicitacao = Long.valueOf(parts[0]);
                String codCanal = parts[1];

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
                    List<CreditRequestItemsEntity> itens = domainItens.stream()
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

    // ── Helpers ───────────────────────────────────────────────────────

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
            List<HistCreditRequestItems> history = historyService.findItemStatusHistory(
                    numSolicitacao, numSolicitacaoItem, codCanal);

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
