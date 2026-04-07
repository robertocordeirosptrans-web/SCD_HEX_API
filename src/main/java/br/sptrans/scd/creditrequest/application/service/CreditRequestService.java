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

import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.creditrequest.adapter.out.jpa.entity.CreditRequestItemsEJpa;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit.ItemRequest;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse.ItemProcessado;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse.ItemRejeitado;
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
import br.sptrans.scd.product.application.service.FeeFareService;
import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.domain.vo.TaxaCalculada;
import br.sptrans.scd.shared.cache.InvalidateOrderCache;
import br.sptrans.scd.shared.exception.ValidationException;
import br.sptrans.scd.shared.idempotency.IdempotencyStore;
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

    private final CreditRequestValidationService validationService;
    private final RechargeLogService rechargeLogService;
    private final CreditRequestRepository creditRequestRepository;
    private final CreditRequestItemsRepository itemRepository;
    private final CreditRequestMapper creditRequestMapper;
    private final HistCreditRequestService historyService;
    private final TransitionSituationValidator transitionValidator;
    private final SituationAscertainedService situationAscertainedService;
    private final IdempotencyStore idempotencyStore;
    private final FeeFareService feeFareService;

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
    public CreateRequestResponse createCreditRequest(
            CreateRequestCredit request,
            String idempotencyKey,
            Long userId) {

        log.info("[createCreditRequest] INICIADO - idempotencyKey={}, request={}", idempotencyKey, request);

        // 1. Idempotência
        Optional<CreateRequestResponse> cached = idempotencyStore.get(idempotencyKey);
        if (cached.isPresent()) {
            log.info("[createCreditRequest] Idempotência: retornando resultado em cache para chave '{}'", idempotencyKey);
            return cached.get();
        }

        // 2. Validações prévias (do segundo método)
        log.info("[createCreditRequest] Validando canal, lote e data de liberação...");
        SalesChannel canal = validationService.validarCanal(request.codCanal());
        validationService.validarDataLiberacao(request.dataLiberacaoCredito());

        if ("S".equalsIgnoreCase(canal.getFlgSupercanal())) {
            log.info("[createCreditRequest] Canal é supercanal, validando subordinados...");
            validationService.validarSubordinadosSupercanal(request.codCanal());
        }

        // 3. Configurações de processamento
        boolean processamentoParcialPermitido = "S".equalsIgnoreCase(canal.getFlgProcessamentoParcial());
        log.info("[createCreditRequest] Processamento parcial permitido? {}", processamentoParcialPermitido);

        List<CreateRequestResponse.ItemProcessado> processados = new ArrayList<>();
        List<CreateRequestResponse.ItemRejeitado> rejeitados = new ArrayList<>();

        int totalItens = request.pedidos().stream().mapToInt(p -> p.itens().size()).sum();
        log.info("[createCreditRequest] Total de itens a processar: {}", totalItens);

        for (CreateRequestCredit.CreditRequest pedido : request.pedidos()) {
            //Validar o numero de lote para cada pedido
            validationService.validarNumLote(pedido.numLote(), request.codCanal(), creditRequestRepository);

            // Verificar se já existe a solicitação na base (SOL_DISTRIBUICOES)
            boolean solicitacaoExiste = creditRequestRepository.findByNumSolicitacaoAndCodCanal(pedido.numSolicitacao(), request.codCanal()).isPresent();
            if (solicitacaoExiste) {
                log.warn("[createCreditRequest] Solicitação {} já existe para o canal {}. Não será criado novo pedido.", pedido.numSolicitacao(), request.codCanal());
                throw new IllegalStateException("Já existe solicitação para este canal: " + pedido.numSolicitacao());
            }

            long numSolicitacaoItemSeq = 1L;
            for (ItemRequest item : pedido.itens()) {
                log.info("[createCreditRequest] Processando pedido={}, item={}, numSolicitacaoItem={}", pedido, item, numSolicitacaoItemSeq);
                processarItemComTryCatchSequencial(canal, pedido, item, request,
                        processados, rejeitados, 0, userId, numSolicitacaoItemSeq);
                numSolicitacaoItemSeq++;
                log.info("[createCreditRequest] Parcial: processados={}, rejeitados={}", processados.size(), rejeitados.size());
            }
        }

        // 5. Validação pós-processamento (do segundo método)
        if (processados.isEmpty() && !rejeitados.isEmpty() && !processamentoParcialPermitido) {
            String motivos = rejeitados.stream()
                    .map(r -> r.numSolicitacao() + ": " + r.motivoRejeicao())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Motivo desconhecido");
            log.warn("[createCreditRequest] Todos os pedidos rejeitados: {}", motivos);
            throw new IllegalStateException("Todos os pedidos foram rejeitados: " + motivos);
        }

        // 6. Cache e resposta
        CreateRequestResponse response = new CreateRequestResponse(
                totalItens,
                processados.size(),
                rejeitados.size(),
                processados,
                rejeitados
        );

        log.info("[createCreditRequest] FINALIZADO - processados={}, rejeitados={}, response={}", processados.size(), rejeitados.size(), response);
        return response;
    }

    // Novo método para processar item com numSolicitacaoItem sequencial
    private void processarItemComTryCatchSequencial(
            SalesChannel canal,
            CreateRequestCredit.CreditRequest pedido,
            ItemRequest item,
            CreateRequestCredit request,
            List<ItemProcessado> processados,
            List<ItemRejeitado> rejeitados,
            int index,
            Long userId,
            long numSolicitacaoItemSeq) {

        Long numSolicitacao = pedido != null ? pedido.numSolicitacao() : null;
        String numLogicoCartao = item != null ? item.numLogicoCartao() : null;
        String codProduto = item != null ? item.codProduto() : null;

        try {
            // Validações por item (melhor que apenas null check)
            if (pedido == null) {
                rejeitados.add(new ItemRejeitado(
                        null, numLogicoCartao, codProduto,
                        "Pedido não encontrado para o índice " + index));
                return;
            }

            if (item == null) {
                rejeitados.add(new ItemRejeitado(
                        numSolicitacao, null, null,
                        "Item não encontrado para o pedido " + numSolicitacao));
                return;
            }

            // Nova validação de item
            validarItem(canal, pedido, item, request);

            // Persistência dentro da transação (atomicidade por item)
            // 1. Salvar pedido principal se ainda não existir
            Optional<CreditRequest> existingRequest = creditRequestRepository.findByNumSolicitacaoAndCodCanal(numSolicitacao, request.codCanal());
            CreditRequest creditRequest;

            if (existingRequest.isEmpty()) {
                creditRequest = mapToCreditRequest(pedido, item, request, userId);
                creditRequestRepository.save(creditRequest);
                historyService.saveRequestStatusHistory(creditRequest, pedido.numSolicitacao(), request.codCanal(), ORIGEM_TRANSICAO);
            } else {
                creditRequest = existingRequest.get();
            }

            // 2. Salvar item com numSolicitacaoItem sequencial
            CreditRequestItems creditRequestItem = mapToCreditRequestItemSequencial(pedido, item, request, creditRequest, userId, numSolicitacaoItemSeq);
            log.info("[DEBUG] Antes de salvar item: numSolicitacao={}, idUsuarioCartao={}, numLogicoCartao={}, codProduto={}, creditRequestItem.numLogicoCartao={}, numSolicitacaoItem={}",
                    numSolicitacao,
                    item.idUsuarioCartao(),
                    item.numLogicoCartao(),
                    item.codProduto(),
                    creditRequestItem.getNumLogicoCartao(),
                    numSolicitacaoItemSeq
            );

             log.info("[DEBUG] Antes de salvar item:  idUsuarioCartao={}, numLogicoCartao={}",
        
                    item.idUsuarioCartao(),
                    item.numLogicoCartao());
            int seqRecarga = rechargeLogService.upsertLogRecarga(
                    item.numLogicoCartao(),
                    userId,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    userId);
            creditRequestItem.setSeqRecarga(seqRecarga);
            itemRepository.save(creditRequestItem);
            historyService.saveItemStatusHistory(creditRequestItem, ORIGEM_TRANSICAO);

            processados.add(new ItemProcessado(
                    numSolicitacao,
                    numLogicoCartao,
                    codProduto,
                    "03"));

        } catch (ValidationException e) {
            // Erros de validação específicos
            rejeitados.add(new ItemRejeitado(
                    numSolicitacao, numLogicoCartao, codProduto,
                    "Validação: " + e.getMessage()));

        } catch (Exception e) {
            // Erros inesperados
            log.error("Erro ao processar pedido {}: {}", numSolicitacao, e.getMessage(), e);
            rejeitados.add(new ItemRejeitado(
                    numSolicitacao, numLogicoCartao, codProduto,
                    "Erro interno: " + e.getMessage()));
        }
    }

    /**
     * Valida um item de pedido de crédito usando o validationService. Lança
     * ValidationException em caso de erro de validação.
     */
    private void validarItem(
            SalesChannel canal,
            CreateRequestCredit.CreditRequest pedido,
            ItemRequest item,
            CreateRequestCredit request) {
        // Validar Comercialização de Produto com o Canal
        List<CreateRequestResponse.ItemRejeitado> rejeitados = new ArrayList<>();
        var produtoCanal = validationService.validarProdutoNoCanal(
            pedido.numSolicitacao(),
            item.numLogicoCartao(),
            item.codProduto(),
            request.codCanal(),
            item.codProduto(),
            rejeitados);
        if (produtoCanal == null) {
            throw new ValidationException("Produto não comercializado ou inativo no canal");
        }

        // Validar vigência da versão do produto
        // validationService.validarVigenciaVersaoProduto(
        //     pedido.numSolicitacao(),
        //     item.numLogicoCartao(),
        //     item.codProduto(),
        //     request.codCanal(),
        //     item.codVersao(),
        //     request.dataLiberacaoCredito(),
        //     rejeitados);

        // Validar limites de recarga
        String erroLimite = validationService.validarLimites(
            request.codCanal(),
            item.codProduto(),
            item.valorTotal());
        if (erroLimite != null) {
            throw new ValidationException(erroLimite);
        }

        // Validar vigência do convênio do canal
        validationService.validarVigenciadoCanal(
                pedido.numSolicitacao(),
                item.numLogicoCartao(),
                item.codProduto(),
                request.codCanal(),
                pedido.canaisDistribuicao(),
                item.codProduto(),
                rejeitados);
        if (!rejeitados.isEmpty()) {
            // Lança a primeira mensagem de rejeição encontrada
            throw new ValidationException(rejeitados.get(0).motivoRejeicao());
        }

        // ===== Validação de Taxas conforme plano =====
        // Buscar taxa vigente para canal/produto e data atual
        Optional<Fee> taxaOpt = feeFareService.findByCanalProduto(request.codCanal(), item.codProduto())
            .stream()
            .filter(f -> f.getDtFinal() == null || f.getDtFinal().isAfter(LocalDateTime.now()))
            .findFirst();
        if (taxaOpt.isEmpty()) {
            throw new ValidationException("Taxa não encontrada para canal/produto vigente");
        }
        Fee taxa = taxaOpt.get();
        // Calcular taxas
        TaxaCalculada taxas = feeFareService.calcularTaxas(
            item.valorTotal(),
            request.codCanal(),
            pedido.numSolicitacao() != null ? pedido.numSolicitacao().toString() : null,
            taxa.getCodTaxa()
        );
        // Log dos valores calculados
        log.info("[TAXAS] Pedido: {}, Cartao: {}, Produto: {}, TaxaAdm Calculada: {}, TaxaServ Calculada: {}", 
            pedido.numSolicitacao(), item.numLogicoCartao(), item.codProduto(), taxas.getValorTaxaAdm(), taxas.getValorTaxaServ());
        // Comparar com valores informados
        BigDecimal vlTxadmInformado = item.vlTxadm() != null ? item.vlTxadm() : BigDecimal.ZERO;
        BigDecimal vlTxservInformado = item.vlTxserv() != null ? item.vlTxserv() : BigDecimal.ZERO;
        if (taxas.getValorTaxaAdm().compareTo(vlTxadmInformado) != 0) {
            throw new ValidationException("303 - Taxa administrativa incorreta");
        }
        if (taxas.getValorTaxaServ().compareTo(vlTxservInformado) != 0) {
            throw new ValidationException("304 - Taxa de serviço incorreta");
        }
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
                    String statusAnterior = item.getCodSituacao();

                    if (acao == ActionStatus.DESBLOQUEAR) {
                        item.setCodSituacao(SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode());
                        itemRepository.save(item);
                        historyService.saveItemStatusHistory(item, ORIGEM_TRANSICAO);
                        itensParaRestaurar.add(creditRequestMapper.toEntityItem(item));
                    } else {
                        String novoStatus = determinarNovoStatus(acao);
                        item.setCodSituacao(novoStatus);

                        if (acao == ActionStatus.PAGO) {
                            log.warn("Item encontrado: NumLogicoCartao={}, CodCanal={}",
                                    item.getNumLogicoCartao(), codCanal);
                            item.setDtPagtoEconomica(dtConfirmaPagto != null ? dtConfirmaPagto : LocalDateTime.now());
                        }

                        itemRepository.save(item);
                        historyService.saveItemStatusHistory(item, ORIGEM_TRANSICAO);

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
                // Atualizar campos do pedido durante o pagamento
                if (acao == ActionStatus.PAGO) {
                    Optional<CreditRequest> pedidoOpt = creditRequestRepository.findByNumSolicitacaoAndCodCanal(numSolicitacao, codCanal);
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

        // Buscar itens individualmente usando findById
        List<CreditRequestItemsEJpa> itens = new ArrayList<>();

        for (long numSolicitacaoItem = 1;; numSolicitacaoItem++) {
            CreditRequestItemsKey key = new CreditRequestItemsKey();
            key.setNumSolicitacao(numSolicitacao);
            key.setNumSolicitacaoItem(numSolicitacaoItem);
            key.setCodCanal(codCanal);
            Optional<CreditRequestItems> opt = itemRepository.findById(key);
            if (opt.isPresent()) {
                // Se necessário converter para EJpa:
                itens.add(creditRequestMapper.toEntityItem(opt.get()));
                // Ou se quiser trabalhar só com domínio, adapte o restante do método
            } else {
                break; // Sai do loop quando não encontrar mais itens
            }
        }
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
                creditRequestRepository.update(numSolicitacao, codCanal, solicitacao);
                historyService.saveRequestStatusHistory(solicitacao, numSolicitacao, codCanal, ORIGEM_TRANSICAO);
                log.info("Histórico de cancelamento registrado para solicitação {}", numSolicitacao);
                return;
            } else if (acao == ActionStatus.PAGO || acao == ActionStatus.ACEITO_PENDENTE_LIQUIDACAO) {
                // Apply transition-specific fields
                solicitacao.setCodSituacao(novoStatusSolicitacao);
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
            boolean temSucesso = statusItens.stream().anyMatch(s
                    -> SituationCreditRequestItems.RECARREGADO.getCode().equals(s)
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

    // ── Validações de Transações ───────────────────────────────────────────────────
    private void validarTransicoes(ActionStatus acao, OrderItemEntry entry) {
        for (Long numSolicitacaoItem : entry.numSolicitacaoItems()) {

            CreditRequestItemsKey itemId = new CreditRequestItemsKey();
            itemId.setNumSolicitacao(entry.numSolicitacao());
            itemId.setNumSolicitacaoItem(numSolicitacaoItem);
            itemId.setCodCanal(entry.codCanal());

            Optional<CreditRequestItems> itemOpt = itemRepository.findById(itemId);
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

                List<CreditRequestItemsEJpa> itens = new ArrayList<>();
                for (PayItemEntry payItem : entry.getValue()) {
                    CreditRequestItemsKey key = new CreditRequestItemsKey();
                    key.setNumSolicitacao(numSolicitacao);
                    key.setNumSolicitacaoItem(payItem.numSolicitacaoItem());
                    key.setCodCanal(codCanal);
                    Optional<CreditRequestItems> opt = itemRepository.findById(key);
                    opt.ifPresent(domainItem -> itens.add(creditRequestMapper.toEntityItem(domainItem)));
                }

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
            case BLOQUEAR ->
                SituationCreditRequestItems.BLOQUEADO.getCode();
            case DESBLOQUEAR ->
                SituationCreditRequestItems.DESBLOQUEIO_SOLICITADO.getCode();
            case CANCELAR ->
                SituationCreditRequestItems.CANCELADO.getCode();
            case PAGO ->
                SituationCreditRequestItems.PAGO.getCode();
            case ACEITO_PENDENTE_LIQUIDACAO ->
                SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode();
            case LIBERAR_RECARGA ->
                SituationCreditRequestItems.LIBERADO_PARA_RECARGA.getCode();
        };
    }

    private String findStatusBeforeBloqueio(Long numSolicitacao, Long numSolicitacaoItem, String codCanal) {
        try {
            List<HistCreditRequestItems> history
                    = historyService.findItemStatusHistory(numSolicitacao, numSolicitacaoItem, codCanal);

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
            List<HistCreditRequest> history
                    = historyService.findRequestStatusHistory(numSolicitacao, codCanal);

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

    /**
     * Mapeia os dados do pedido e item do request para a entidade de domínio
     * CreditRequest.
     */
    private CreditRequest mapToCreditRequest(CreateRequestCredit.CreditRequest pedido, ItemRequest item, CreateRequestCredit request, Long userId) {
        CreditRequest cr = new CreditRequest();
        cr.setNumSolicitacao(pedido.numSolicitacao());
        cr.setCodCanal(request.codCanal());
        cr.setNumLote(pedido.numLote());
        cr.setDtCadastro(LocalDateTime.now());
        cr.setDtManutencao(LocalDateTime.now());
        cr.setDtSolicitacao(request.dataGeracao());
        cr.setDtPrevLiberacao(request.dataLiberacaoCredito());
        cr.setCodSituacao("03");
        cr.setVlTotal(item.valorTotal());
        cr.setCodTipoDocumento("1");
        cr.setIdUsuarioCadastro(userId);
        cr.setIdUsuarioManutencao(userId);
        return cr;
    }

    /**
     * Mapeia os dados do pedido e item do request para a entidade de domínio
     * CreditRequestItems.
     */
    // Novo método para mapear item com numSolicitacaoItem sequencial
    private CreditRequestItems mapToCreditRequestItemSequencial(CreateRequestCredit.CreditRequest pedido, ItemRequest item, CreateRequestCredit request, CreditRequest creditRequest, Long userId, long numSolicitacaoItemSeq) {
        CreditRequestItems cri = new CreditRequestItems();
        CreditRequestItemsKey key = new CreditRequestItemsKey();
        key.setNumSolicitacao(pedido.numSolicitacao());
        key.setNumSolicitacaoItem(numSolicitacaoItemSeq);
        key.setCodCanal(request.codCanal());
        cri.setId(key);
        cri.setSolicitacao(creditRequest);
        cri.setCodCanal(request.codCanal());
        cri.setIdUsuarioCadastro(userId);
        cri.setIdUsuarioManutencao(userId);
        cri.setIdUsuarioCartao(item.idUsuarioCartao());
        cri.setNumLogicoCartao(item.numLogicoCartao());
        cri.setCodProduto(item.codProduto());
        cri.setCodVersao(item.codVersao());
        cri.setCodSituacao("03"); // Situação inicial, ajustar conforme regra
        cri.setQtdItem(0); // Ajuste se necessário
        cri.setVlUnitario(item.vlUnitario());
        cri.setVlItem(item.valorTotal());
        cri.setDtCadastro(LocalDateTime.now());
        cri.setDtManutencao(LocalDateTime.now());
        cri.setVlTxadm(item.vlTxadm());
        cri.setVlTxserv(item.vlTxserv());
        cri.setVlTxtotal(item.vlTxadm().add(item.vlTxserv()));
        cri.setSeqRecarga(1);
        cri.setCodTipoDocumento("2");
        return cri;
    }

}
