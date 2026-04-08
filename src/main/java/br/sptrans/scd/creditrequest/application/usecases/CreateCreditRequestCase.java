package br.sptrans.scd.creditrequest.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.sptrans.scd.channel.domain.SalesChannel;
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
import br.sptrans.scd.creditrequest.domain.CreditRequestItemsKey;
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

    /** Logger de auditoria — registra operações sensíveis sem expor dados de cartão. */
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT." + CreateCreditRequestCase.class.getName());

    static final String ORIGEM_TRANSICAO = "pedido_credito_scd";

    private final CreditRequestValidationService validationService;
    private final RechargeLogService rechargeLogService;
    private final CreditRequestPort creditRequestRepository;
    private final CreditRequestItemsPort itemRepository;
    private final HistCreditRequestService historyService;
    private final IdempotencyStore idempotencyStore;
    private final FeeFareService feeFareService;

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
            auditLog.info("[createCreditRequest] Resposta idempotente retornada - idempotencyKey={}", idempotencyKey);
            return cached.get();
        }

        // 2. Validações prévias
        auditLog.info("[createCreditRequest] Validando canal e data de liberação - codCanal={}", request.codCanal());
        SalesChannel canal = validationService.validarCanal(request.codCanal());
        validationService.validarDataLiberacao(request.dataLiberacaoCredito());

        if ("S".equalsIgnoreCase(canal.getFlgSupercanal())) {
            auditLog.info("[createCreditRequest] Supercanal detectado, validando subordinados - codCanal={}", request.codCanal());
            validationService.validarSubordinadosSupercanal(request.codCanal());
        }

        boolean processamentoParcialPermitido = "S".equalsIgnoreCase(canal.getFlgProcessamentoParcial());

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
                throw new IllegalStateException("Já existe solicitação para este canal: " + pedido.numSolicitacao());
            }

            // O domínio CreditRequest é criado uma única vez e reutilizado por todos os itens
            // do pedido, eliminando a query extra por item que existia anteriormente.
            CreditRequest creditRequestDomain = null;
            long numSolicitacaoItemSeq = 1L;
            for (ItemRequest item : pedido.itens()) {
                auditLog.debug("[createCreditRequest] Processando item - numSolicitacao={}, numItem={}",
                        pedido.numSolicitacao(), numSolicitacaoItemSeq);
                creditRequestDomain = processarItem(
                        canal, pedido, item, request,
                        processados, rejeitados,
                        userId, numSolicitacaoItemSeq,
                        creditRequestDomain);
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
            auditLog.warn("[createCreditRequest] Todos os itens rejeitados - codCanal={}", request.codCanal());
            throw new IllegalStateException("Todos os pedidos foram rejeitados: " + motivos);
        }

        CreateRequestResponse response = new CreateRequestResponse(
                totalItens,
                processados.size(),
                rejeitados.size(),
                processados,
                rejeitados
        );

        // Salvar no cache de idempotência ANTES de retornar — impede reprocessamento em retry
        idempotencyStore.put(idempotencyKey, response);

        auditLog.info("[AUDIT] Pedido de crédito processado - userId={}, codCanal={}, processados={}, rejeitados={}",
                userId, request.codCanal(), processados.size(), rejeitados.size());
        auditLog.info("[createCreditRequest] FINALIZADO - processados={}, rejeitados={}", processados.size(), rejeitados.size());

        return response;
    }

    /**
     * Processa um único item dentro de um pedido.
     *
     * <p>Recebe o {@code creditRequestDomain} em cache (null no primeiro item) para
     * reutilizá-lo nos itens seguintes, evitando queries repetidas ao banco.
     * Retorna o domain object persistido (ou o recebido), para reuso pelo chamador.</p>
     */
    private CreditRequest processarItem(
            SalesChannel canal,
            CreateRequestCredit.CreditRequest pedido,
            ItemRequest item,
            CreateRequestCredit request,
            List<ItemProcessado> processados,
            List<ItemRejeitado> rejeitados,
            Long userId,
            long numSolicitacaoItemSeq,
            CreditRequest creditRequestDomain) {

        Long numSolicitacao = pedido.numSolicitacao();
        String codProduto = item.codProduto();

        try {
            // Validações do item antes de qualquer persistência
            validarItem(canal, pedido, item, request);

            // CreditRequest é persistido uma única vez por pedido; reutilizado nos demais itens
            if (creditRequestDomain == null) {
                CreditRequest novaRequest = mapToCreditRequest(pedido, item, request, userId);
                creditRequestRepository.save(novaRequest);
                // Atribuição após o save bem-sucedido — se save lançar, permanece null
                creditRequestDomain = novaRequest;
                historyService.saveRequestStatusHistory(creditRequestDomain, numSolicitacao,
                        request.codCanal(), ORIGEM_TRANSICAO);
                auditLog.info("[AUDIT] Solicitação criada - numSolicitacao={}, codCanal={}, userId={}",
                        numSolicitacao, request.codCanal(), userId);
            }

            // Construir item e vinculá-lo ao domain via addItem (valida cancelado/bloqueado)
            CreditRequestItems creditRequestItem = mapToCreditRequestItemSequencial(
                    pedido, item, request, creditRequestDomain, userId, numSolicitacaoItemSeq);
            creditRequestDomain.addItem(creditRequestItem);

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

            auditLog.info("[AUDIT] Item criado - numSolicitacao={}, numItem={}, codProduto={}, userId={}",
                    numSolicitacao, numSolicitacaoItemSeq, codProduto, userId);

            processados.add(new ItemProcessado(
                    numSolicitacao,
                    item.numLogicoCartao(),
                    codProduto,
                    SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode()));

        } catch (ValidationException e) {
            auditLog.warn("[createCreditRequest] Validação falhou - numSolicitacao={}, numItem={}, motivo={}",
                    numSolicitacao, numSolicitacaoItemSeq, e.getMessage());
            rejeitados.add(new ItemRejeitado(
                    numSolicitacao, item.numLogicoCartao(), codProduto,
                    "Validação: " + e.getMessage()));

        } catch (Exception e) {
            auditLog.error("[createCreditRequest] Erro inesperado - numSolicitacao={}, numItem={}",
                    numSolicitacao, numSolicitacaoItemSeq, e);
            rejeitados.add(new ItemRejeitado(
                    numSolicitacao, item.numLogicoCartao(), codProduto,
                    "Erro interno: " + e.getMessage()));
        }

        return creditRequestDomain;
    }



    private void validarItem(
            SalesChannel canal,
            CreateRequestCredit.CreditRequest pedido,
            ItemRequest item,
            CreateRequestCredit request) {

        List<ItemRejeitado> rejeitados = new ArrayList<>();
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

        String erroLimite = validationService.validarLimites(
                request.codCanal(),
                item.codProduto(),
                item.valorTotal());
        if (erroLimite != null) {
            throw new ValidationException(erroLimite);
        }

        validationService.validarVigenciadoCanal(
                pedido.numSolicitacao(),
                item.numLogicoCartao(),
                item.codProduto(),
                request.codCanal(),
                pedido.canaisDistribuicao(),
                item.codProduto(),
                rejeitados);
        if (!rejeitados.isEmpty()) {
            throw new ValidationException(rejeitados.get(0).motivoRejeicao());
        }

        // Validação de Taxas
        Optional<Fee> taxaOpt = feeFareService.findByCanalProduto(request.codCanal(), item.codProduto())
                .stream()
                .filter(f -> f.getDtFinal() == null || f.getDtFinal().isAfter(LocalDateTime.now()))
                .findFirst();
        if (taxaOpt.isEmpty()) {
            throw new ValidationException("Taxa não encontrada para canal/produto vigente");
        }
        Fee taxa = taxaOpt.get();
        TaxaCalculada taxas = feeFareService.calcularTaxas(
                item.valorTotal(),
                request.codCanal(),
                pedido.numSolicitacao() != null ? pedido.numSolicitacao().toString() : null,
                taxa.getCodTaxa());
        // numLogicoCartao omitido do log — dado sensível
        auditLog.info("[TAXAS] Pedido={}, Produto={}, TaxaAdm={}, TaxaServ={}",
                pedido.numSolicitacao(), item.codProduto(),
                taxas.getValorTaxaAdm(), taxas.getValorTaxaServ());

        BigDecimal vlTxadmInformado = item.vlTxadm() != null ? item.vlTxadm() : BigDecimal.ZERO;
        BigDecimal vlTxservInformado = item.vlTxserv() != null ? item.vlTxserv() : BigDecimal.ZERO;
        if (taxas.getValorTaxaAdm().compareTo(vlTxadmInformado) != 0) {
            throw new ValidationException("303 - Taxa administrativa incorreta");
        }
        if (taxas.getValorTaxaServ().compareTo(vlTxservInformado) != 0) {
            throw new ValidationException("304 - Taxa de serviço incorreta");
        }
    }

    private CreditRequest mapToCreditRequest(
            CreateRequestCredit.CreditRequest pedido,
            ItemRequest item,
            CreateRequestCredit request,
            Long userId) {
        CreditRequest cr = new CreditRequest();
        cr.setNumSolicitacao(pedido.numSolicitacao());
        cr.setCodCanal(request.codCanal());
        cr.setNumLote(pedido.numLote());
        cr.setDtCadastro(LocalDateTime.now());
        cr.setDtManutencao(LocalDateTime.now());
        cr.setDtSolicitacao(request.dataGeracao());
        cr.setDtPrevLiberacao(request.dataLiberacaoCredito());
        cr.setCodSituacao(SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode());
        cr.setVlTotal(item.valorTotal());
        cr.setCodTipoDocumento("1");
        cr.setIdUsuarioCadastro(userId);
        cr.setIdUsuarioManutencao(userId);
        return cr;
    }

    private CreditRequestItems mapToCreditRequestItemSequencial(
            CreateRequestCredit.CreditRequest pedido,
            ItemRequest item,
            CreateRequestCredit request,
            CreditRequest creditRequest,
            Long userId,
            long numSolicitacaoItemSeq) {
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
        cri.setCodSituacao(SituationCreditRequestItems.ACEITO_PENDENTE_LIQUIDACAO.getCode());
        cri.setQtdItem(0);
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
