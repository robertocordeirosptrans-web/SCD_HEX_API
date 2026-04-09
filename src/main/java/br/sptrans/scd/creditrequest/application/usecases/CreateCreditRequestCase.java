
package br.sptrans.scd.creditrequest.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import br.sptrans.scd.channel.domain.ProductChannel;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit.ItemRequest;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse.ItemProcessado;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestResponse.ItemRejeitado;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestItemsPort;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestPort;
import br.sptrans.scd.creditrequest.application.service.CreditRequestValidationService;
import br.sptrans.scd.creditrequest.application.service.HistCreditRequestService;
import br.sptrans.scd.creditrequest.application.service.RechargeLogService;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.creditrequest.domain.enums.SituationCreditRequestItems;
import br.sptrans.scd.product.application.service.FeeFareService;
import br.sptrans.scd.product.domain.Fee;
import br.sptrans.scd.product.domain.vo.TaxaCalculada;
import br.sptrans.scd.shared.exception.ValidationException;
import br.sptrans.scd.shared.idempotency.IdempotencyStore;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateCreditRequestCase {

        /**
         * Logger de auditoria — registra operações sensíveis sem expor dados de cartão.
         */
        private static final Logger auditLog = LoggerFactory
                        .getLogger("AUDIT." + CreateCreditRequestCase.class.getName());

        static final String ORIGEM_TRANSICAO = "pedido_credito_scd";

        private final CreditRequestValidationService validationService;
        private final RechargeLogService rechargeLogService;
        private final CreditRequestPort creditRequestRepository;
        private final CreditRequestItemsPort itemRepository;
        private final HistCreditRequestService historyService;
        private final IdempotencyStore<CreateRequestResponse> idempotencyStore;
        private final FeeFareService feeFareService;
        private final CreditRequestMapper creditRequestMapper;

        public CreateRequestResponse execute(
                        CreateRequestCredit request,
                        String idempotencyKey,
                        Long userId) {

                // Não logar o objeto 'request' — contém números de cartão e dados sensíveis
                auditLog.info("[createCreditRequest] INICIADO - idempotencyKey={}, codCanal={}, totalPedidos={}",
                                idempotencyKey, request.codCanal(), request.pedidos().size());

                // 1. Idempotency check — retorna resposta em cache sem reprocessar
                Optional<CreateRequestResponse> cached = idempotencyStore.get(idempotencyKey);
                if (cached.isPresent()) {
                        auditLog.info("[createCreditRequest] Resposta idempotente retornada - idempotencyKey={}",
                                        idempotencyKey);
                        return cached.get();
                }

                // 2. Validações prévias
                auditLog.info("[createCreditRequest] Validando canal e data de liberação - codCanal={}",
                                request.codCanal());
                SalesChannel canal = validationService.validarCanal(request.codCanal());
                validationService.validarDataLiberacao(request.dataLiberacaoCredito());

                if ("S".equalsIgnoreCase(canal.getFlgSupercanal())) {
                        auditLog.info("[createCreditRequest] Supercanal detectado, validando subordinados - codCanal={}",
                                        request.codCanal());
                        validationService.validarSubordinadosSupercanal(request.codCanal());
                }

                boolean processamentoParcialPermitido = canal.isProcessamentoParcialHabilitado();
                

                List<ItemProcessado> processados = new ArrayList<>();
                List<ItemRejeitado> rejeitados = new ArrayList<>();

                int totalItens = request.pedidos().stream().mapToInt(p -> p.itens().size()).sum();
                auditLog.info("[createCreditRequest] Pedidos={}, itens={}", request.pedidos().size(), totalItens);

                for (CreateRequestCredit.CreditRequest pedido : request.pedidos()) {
                    // Validar número de lote (uma query por pedido)
                    validationService.validarNumLote(pedido.numLote(), request.codCanal());

                    // Verificar duplicata — uma única query por pedido antes do loop de itens
                    if (creditRequestRepository
                            .findByNumSolicitacaoAndCodCanal(pedido.numSolicitacao(), request.codCanal())
                            .isPresent()) {
                        auditLog.warn("[createCreditRequest] Duplicata bloqueada - numSolicitacao={}, codCanal={}",
                                pedido.numSolicitacao(), request.codCanal());
                        throw new IllegalStateException(
                                "Já existe solicitação para este canal: " + pedido.numSolicitacao());
                    }

                    // Pré-carregar contextos de validação por canal/produto
                    Map<String, ItemValidationContext> contextos = new HashMap<>();
                    CreditRequest creditRequestDomain = null;
                    long numSolicitacaoItemSeq = 1L;
                    for (ItemRequest item : pedido.itens()) {
                        auditLog.debug("[createCreditRequest] Processando item - numSolicitacao={}, numItem={}",
                                pedido.numSolicitacao(), numSolicitacaoItemSeq);
                        // Chave canal+produto
                        String chave = request.codCanal() + ":" + item.codProduto();
                        ItemValidationContext ctx = contextos.computeIfAbsent(
                                chave, k -> prepararContextoValidacao(request.codCanal(), item.codProduto()));
                        ItemResult result = processarItemComContextoResult(
                                pedido, item, request,
                                userId, numSolicitacaoItemSeq,
                                creditRequestDomain, ctx);
                        if (result instanceof ItemResult.Valid valid) {
                            creditRequestDomain = valid.creditRequestDomain();
                            processados.add(valid.itemProcessado());
                        } else if (result instanceof ItemResult.Invalid invalid) {
                            rejeitados.add(invalid.itemRejeitado());
                        }
                        numSolicitacaoItemSeq++;
                    }

                    auditLog.info("[createCreditRequest] Pedido concluído - numSolicitacao={}, processados={}, rejeitados={}",
                            pedido.numSolicitacao(), processados.size(), rejeitados.size());
                }

                // Validação pós-processamento
                if (processados.isEmpty() && !rejeitados.isEmpty() && !processamentoParcialPermitido) {
                        String motivos = rejeitados.stream()
                                        .map(r -> r.numSolicitacao() + ": " + r.motivoRejeicao())
                                        .reduce((a, b) -> a + "; " + b)
                                        .orElse("Motivo desconhecido");
                        auditLog.warn("[createCreditRequest] Todos os itens rejeitados - codCanal={}",
                                        request.codCanal());
                        throw new IllegalStateException("Todos os pedidos foram rejeitados: " + motivos);
                }

                CreateRequestResponse response = new CreateRequestResponse(
                                totalItens,
                                processados.size(),
                                rejeitados.size(),
                                processados,
                                rejeitados);

                // Salvar no cache de idempotência ANTES de retornar — impede reprocessamento em
                // retry
                idempotencyStore.put(idempotencyKey, response);

                auditLog.info("[AUDIT] Pedido de crédito processado - userId={}, codCanal={}, processados={}, rejeitados={}",
                                userId, request.codCanal(), processados.size(), rejeitados.size());
                auditLog.info("[createCreditRequest] FINALIZADO - processados={}, rejeitados={}", processados.size(),
                                rejeitados.size());

                return response;
        }

        // Nova versão: validação usando contexto pré-carregado
        private void validarItemComContexto(
                        ItemValidationContext ctx,
                        CreateRequestCredit.CreditRequest pedido,
                        ItemRequest item,
                        CreateRequestCredit request) {
                // Lista LOCAL — isolada por item, nunca compartilhada entre iterações
                List<ItemRejeitado> rejeitadosLocal = new ArrayList<>();

                // produtoCanal, limite, taxaVigente já carregados no contexto
                if (ctx.produtoCanal() == null) {
                        throw new ValidationException("Produto não comercializado ou inativo no canal");
                }

                if (ctx.limite() != null) {
                        String erroLimite = validationService.validarLimites(
                                request.codCanal(),
                                item.codProduto(),
                                item.valorTotal());
                        if (erroLimite != null) {
                                throw new ValidationException(erroLimite);
                        }
                }

                validationService.validarVigenciadoCanal(
                        pedido.numSolicitacao(),
                        item.numLogicoCartao(),
                        item.codProduto(),
                        request.codCanal(),
                        pedido.canaisDistribuicao(),
                        item.codProduto(),
                        rejeitadosLocal);
                if (!rejeitadosLocal.isEmpty()) {
                        throw new ValidationException(rejeitadosLocal.get(0).motivoRejeicao());
                }

                // Validação de Taxas
                Fee taxa = ctx.taxaVigente();
                if (taxa == null) {
                        throw new ValidationException("Taxa não encontrada para canal/produto vigente");
                }

                TaxaCalculada taxas = feeFareService.calcularTaxas(
                        item.valorTotal(),
                        request.codCanal(),
                        pedido.numSolicitacao().toString(),
                        taxa.getCodTaxa());

                // Normaliza escala para 2 casas decimais (padrão financeiro)
                BigDecimal valorTaxaAdmCalc = taxas.getValorTaxaAdm() != null
                        ? taxas.getValorTaxaAdm().setScale(2, java.math.RoundingMode.HALF_UP)
                        : BigDecimal.ZERO.setScale(2);
                BigDecimal valorTaxaServCalc = taxas.getValorTaxaServ() != null
                        ? taxas.getValorTaxaServ().setScale(2, java.math.RoundingMode.HALF_UP)
                        : BigDecimal.ZERO.setScale(2);
                BigDecimal vlTxadmInformado = item.vlTxadm().setScale(2, java.math.RoundingMode.HALF_UP);
                BigDecimal vlTxservInformado = item.vlTxserv().setScale(2, java.math.RoundingMode.HALF_UP);

                auditLog.warn("[TAXAS] Pedido={}, Produto={}, TaxaAdmCalc={}, TaxaAdmInf={}, TaxaServCalc={}, TaxaServInf={}",
                        pedido.numSolicitacao(), item.codProduto(),
                        valorTaxaAdmCalc, vlTxadmInformado, valorTaxaServCalc, vlTxservInformado);

                if (valorTaxaAdmCalc.compareTo(vlTxadmInformado) != 0) {
                        throw new ValidationException("303 - Taxa administrativa incorreta");
                }
                if (valorTaxaServCalc.compareTo(vlTxservInformado) != 0) {
                        throw new ValidationException("304 - Taxa de serviço incorreta");
                }

                // Validar vigência da versão do produto
                validationService.validarVigenciaVersaoProduto(
                        pedido.numSolicitacao(),
                        item.numLogicoCartao(),
                        item.codProduto(),
                        request.codCanal(),
                        item.codVersao(),
                        request.dataLiberacaoCredito(),
                        rejeitadosLocal);
                if (!rejeitadosLocal.isEmpty()) {
                        throw new ValidationException(rejeitadosLocal.get(0).motivoRejeicao());
                }
        }

        // Nova versão: processar item usando contexto

        /**
         * Refatorado para seguir Result Pattern: retorna ItemResult (Valid ou Invalid) ao invés de lançar exceção e acumular em listas externas.
         */
        private ItemResult processarItemComContextoResult(
                CreateRequestCredit.CreditRequest pedido,
                ItemRequest item,
                CreateRequestCredit request,
                Long userId,
                long numSolicitacaoItemSeq,
                CreditRequest creditRequestDomain,
                ItemValidationContext ctx) {

            String codProduto = item.codProduto();
            try {
                // Validações do item antes de qualquer persistência
                validarItemComContexto(ctx, pedido, item, request);

                // CreditRequest é persistido uma única vez por pedido; reutilizado nos demais itens
                CreditRequest domain = creditRequestDomain;
                if (domain == null) {
                    CreditRequest novaRequest = creditRequestMapper.fromRequest(
                            pedido, item, request, userId);
                    creditRequestRepository.save(novaRequest);
                    domain = novaRequest;
                    historyService.saveRequestStatusHistory(domain, pedido.numSolicitacao(),
                            request.codCanal(), ORIGEM_TRANSICAO);
                    auditLog.info("[AUDIT] Solicitação criada - numSolicitacao={}, codCanal={}, userId={}",
                            pedido.numSolicitacao(), request.codCanal(), userId);
                }

                CreditRequestItems creditRequestItem = creditRequestMapper.fromRequestItem(
                        pedido, item, request, domain, userId, numSolicitacaoItemSeq);
                domain.addItem(creditRequestItem);

                int seqRecarga = rechargeLogService.upsertLogRecarga(
                        item.numLogicoCartao(),
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        userId);
                creditRequestItem.setSeqRecarga(seqRecarga);

                itemRepository.save(creditRequestItem);
                historyService.saveItemStatusHistoryBatch(List.of(creditRequestItem), ORIGEM_TRANSICAO);

                auditLog.info("[AUDIT] Item criado - numSolicitacao={}, numItem={}, codProduto={}, userId={}",
                        pedido.numSolicitacao(), numSolicitacaoItemSeq, codProduto, userId);

                ItemProcessado itemProcessado = new ItemProcessado(
                        pedido.numSolicitacao(),
                        item.numLogicoCartao(),
                        codProduto,
                        SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode());
                return new ItemResult.Valid(domain, itemProcessado);

            } catch (ValidationException e) {
                auditLog.warn("[createCreditRequest] Validação falhou - numSolicitacao={}, numItem={}, motivo={}",
                        pedido.numSolicitacao(), numSolicitacaoItemSeq, e.getMessage());
                ItemRejeitado rejeitado = new ItemRejeitado(
                        pedido.numSolicitacao(), item.numLogicoCartao(), codProduto,
                        "Validação: " + e.getMessage());
                return new ItemResult.Invalid(rejeitado);
            } catch (DataAccessException e) {
                // Propaga erro de infraestrutura para rollback
                throw e;
            } catch (Exception e) {
                auditLog.error("[createCreditRequest] Erro inesperado - numSolicitacao={}, numItem={}",
                        pedido.numSolicitacao(), numSolicitacaoItemSeq, e);
                ItemRejeitado rejeitado = new ItemRejeitado(
                        pedido.numSolicitacao(), item.numLogicoCartao(), codProduto,
                        "Erro interno: " + e.getMessage());
                return new ItemResult.Invalid(rejeitado);
            }
        }



        // Contexto de validação para evitar queries repetidas por canal/produto
        record ItemValidationContext(
                ProductChannel produtoCanal,
                RechargeLimit limite,
                Fee taxaVigente
        ) {}

        // Pré-carrega dados invariantes para canal/produto
        private ItemValidationContext prepararContextoValidacao(String codCanal, String codProduto) {
                ProductChannel produtoCanal = validationService.validarProdutoNoCanal(
                        null, null, codProduto, codCanal, codProduto, new ArrayList<>());
                RechargeLimit limite = validationService.getRechargeLimit(codCanal, codProduto);
                Fee taxaVigente = feeFareService.findByCanalProduto(codCanal, codProduto)
                        .stream()
                        .filter(f -> f.getDtFinal() == null || f.getDtFinal().isAfter(LocalDateTime.now()))
                        .findFirst()
                        .orElse(null);
                return new ItemValidationContext(produtoCanal, limite, taxaVigente);
        }

        
        /**
         * Representa o resultado de uma validação/processamento de item, seguindo o Result Pattern.
         */
        public sealed interface ItemResult permits ItemResult.Valid, ItemResult.Invalid {
                record Valid(CreditRequest creditRequestDomain, ItemProcessado itemProcessado) implements ItemResult {}
                record Invalid(ItemRejeitado itemRejeitado) implements ItemResult {}
        }
}
