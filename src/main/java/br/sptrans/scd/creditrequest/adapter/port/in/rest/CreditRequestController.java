package br.sptrans.scd.creditrequest.adapter.port.in.rest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.BlockCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.CancelCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.PayCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.PayItemEntry;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.SearchCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.UnblockCommand;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageRequest;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageResponse;
import br.sptrans.scd.creditrequest.application.port.in.dto.UpdateRequestCredit;
import br.sptrans.scd.creditrequest.domain.enums.ActionStatus;
import br.sptrans.scd.shared.idempotency.IdempotencyStore;
import br.sptrans.scd.shared.idempotency.InMemoryIdempotencyStore;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/pedidos")
@Tag(name = "Pedidos", description = "API para gestão de pedidos de crédito")
public class CreditRequestController {

    private final CreditRequestManagementUseCase creditRequestManagementUseCase;
    private final CreditRequestMapper creditRequestMapper;
    private static final Logger log = LoggerFactory.getLogger(CreditRequestController.class);

    // Idempotency store (in-memory, para produção use uma implementação persistente)
    private static final IdempotencyStore<ResponseEntity<?>> idempotencyStore = new InMemoryIdempotencyStore<>();

    /**
     * Busca pedidos com paginação por cursor. Classificação automática do modo
     * de busca.
     */
    @PostMapping("/buscar")
    @ResponseBody
    @Operation(
            summary = "Listar pedidos com cursor-based pagination e modos de busca dinâmicos",
            description = """
            Busca pedidos usando cursor-based pagination para melhor performance.
            O backend classifica automaticamente o modo de busca baseado nos filtros fornecidos.
            
            **Modos de Busca (Classificação Automática):**
            
            1. **SPECIFIC (Busca Específica - Fast Path)**
               - Acionado quando: numSolicitacao é fornecido
               - Características:
                 * Retorna no máximo 1 registro
                 * Paginação por cursor desabilitada
                 * Caminho de execução rápido usando índice primário
                 * Ideal para busca por ID específico
            
            2. **OPERATIONAL (Busca Operacional - Padrão)**
               - Acionado quando: filtros operacionais estão presentes
               - Filtros operacionais: codCanal, codSituacao, dtInicio/dtFim
               - Características:
                 * Paginação por cursor habilitada
                 * Limite máximo: 100 registros por página
                 * Requer pelo menos um filtro de alta seletividade
                 * Ideal para listagens e consultas do dia a dia
            
            3. **ANALYTICAL (Busca Analítica - Complexa)**
               - Acionado quando: múltiplos filtros complexos estão presentes (2+)
               - Filtros complexos: numLote, codProduto, codLogin, vlTotal, datas adicionais
               - Características:
                 * Paginação por cursor habilitada
                 * Limite máximo: 50 registros por página
                 * Requer múltiplos filtros seletivos
                 * Ideal para auditorias e investigações
            
            **Filtros Suportados:**
            - codCanal: Código do canal
            - codSituacao: Código da situação
            - numLote: Número do lote
            - numSolicitacao: Número da solicitação (ativa modo SPECIFIC)
            - codProduto: Código do produto (busca nos itens)
            - codFormaPagto: Código da forma de pagamento
            - vlTotalMin/vlTotalMax: Faixa de valor total
            - Filtros de data: dtInicio/dtFim, dtLiberacaoEfetiva, dtPagtoEconomica, dtFinanceira, dtAlteracao
            
            **Paginação:**
            - cursor: Cursor opaco da página anterior (omitir para primeira página)
            - limit: Número de registros por página (ajustado automaticamente pelo modo)
            
            **Resposta:**
            - data: Lista de pedidos
            - nextCursor: Cursor para próxima página (null se não houver mais ou modo SPECIFIC)
            - hasMore: Indica se há mais páginas
            - count: Número de itens retornados
            - metadata.searchMode: Modo de busca utilizado (SPECIFIC, OPERATIONAL, ANALYTICAL)
            - metadata.executionTimeMs: Tempo de execução da query
            
            **Observabilidade:**
            O modo de busca, filtros aplicados, cursor e tempo de execução são registrados
            automaticamente para diagnóstico e otimização contínua.
            """
    )
    public CursorPageResponse<CreditRequestDTO> buscarPedidos(@RequestBody CursorPageRequest request) {
        // Monta o comando de busca a partir do DTO de request
        SearchCommand comando = new SearchCommand(
                request.getCursor() != null ? parseLongOrNull(request.getCursor()) : null,
                request.getCodCanal(),
                request.getCodCanal(),
                request.getCodSituacao(),
                request.getNumLote(),
                request.getCodFormaPagto(),
                request.getDtInicio(),
                request.getDtFim(),
                request.getDtLiberacaoEfetivaInicio(),
                request.getDtLiberacaoEfetivaFim(),
                request.getDtPagtoEconomicaInicio(),
                request.getDtPagtoEconomicaFim(),
                request.getDtFinanceiraInicio(),
                request.getDtFinanceiraFim(),
                request.getDtAlteracaoInicio(),
                request.getDtAlteracaoFim(),
                request.getVlTotalMin(),
                request.getVlTotalMax(),
                null // SearchMode pode ser classificado internamente
        );

        var page = creditRequestManagementUseCase.findAll(comando);
        var dtos = creditRequestMapper.toDTOList(page.content());

        return CursorPageResponse.<CreditRequestDTO>builder()
                .data(dtos)
                .nextCursor(page.nextCursorNumSolicitacao())
                .hasMore(page.hasNext())
                .count(page.size())
                .build();
    }

    private static Long parseLongOrNull(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @PostMapping("/alterar-status")
    @ResponseBody
    @Operation(summary = "Alterar status de pedidos de crédito", description = "Altera o status dos pedidos conforme ação informada.")
    public void alterarStatus(@RequestBody UpdateRequestCredit request) {
        ActionStatus acao = request.getAcao();
        switch (acao) {
            case BLOQUEAR ->
                creditRequestManagementUseCase.block(
                        new BlockCommand(
                                request.getItensPermitidos().stream()
                                        .map(item -> new CreditRequestManagementUseCase.OrderItemEntry(
                                        item.getNumSolicitacao(),
                                        request.getCodCanal(),
                                        item.getItem() != null ? List.of(item.getItem().getNumSolicitacaoItem()) : List.of()
                                ))
                                        .collect(Collectors.toList()),
                                null,
                                request.getObservacao()
                        )
                );
            case DESBLOQUEAR ->
                creditRequestManagementUseCase.unblock(
                        new UnblockCommand(
                                request.getItensPermitidos().stream()
                                        .map(item -> new CreditRequestManagementUseCase.OrderItemEntry(
                                        item.getNumSolicitacao(),
                                        request.getCodCanal(),
                                        item.getItem() != null ? List.of(item.getItem().getNumSolicitacaoItem()) : List.of()
                                ))
                                        .collect(Collectors.toList()),
                                null,
                                request.getObservacao()
                        )
                );
            case CANCELAR ->
                creditRequestManagementUseCase.cancel(
                        new CancelCommand(
                                request.getItensPermitidos().stream()
                                        .map(item -> new CreditRequestManagementUseCase.OrderItemEntry(
                                        item.getNumSolicitacao(),
                                        request.getCodCanal(),
                                        item.getItem() != null ? List.of(item.getItem().getNumSolicitacaoItem()) : List.of()
                                ))
                                        .collect(Collectors.toList()),
                                null,
                                request.getObservacao()
                        )
                );
            case PAGO ->
                creditRequestManagementUseCase.pay(
                        new PayCommand(
                                request.getItensPermitidos().stream()
                                        .map(item -> {
                                            var detail = item.getItem();
                                            return new PayItemEntry(
                                                    item.getNumSolicitacao(),
                                                    detail != null ? detail.getNumSolicitacaoItem() : null,
                                                    request.getCodCanal(),
                                                    detail != null ? detail.getCodProduto() : null,
                                                    detail != null ? detail.getCodSituacao() : null,
                                                    detail != null ? detail.getVlItem() : null,
                                                    detail != null ? detail.getVlTxadm() : null,
                                                    detail != null ? detail.getVlTxserv() : null
                                            );
                                        })
                                        .collect(Collectors.toList()),
                                null,
                                request.getCodFormaPagto(),
                                request.getVlPago() != null ? BigDecimal.valueOf(request.getVlPago()) : null,
                                null
                        )
                );
            default ->
                throw new IllegalArgumentException("Ação não suportada: " + acao);
        }
    }

    @PostMapping("create")
    @Operation(
            summary = "Criar pedidos de crédito em lote",
            description = """
            Processa um lote de pedidos de crédito realizando todas as validações:
            
            **Validações de nível de lote:**
            - Canal existente e ativo
            - Número de lote sem duplicidade
            - Data de liberação válida e futura
            - Supercanal com subordinados ativos (quando aplicável)
            
            **Validações por item:**
            - Número do pedido sem duplicidade no banco
            - Canal de distribuição ativo e com convênio vigente
            - Produto existente e comercializado pelo canal
            - Compatibilidade do produto com o tipo de cartão
            - Limites mínimo e máximo de valor de recarga
            - Valor unitário obrigatório e positivo
            - Cálculo e validação das taxas (com isenção por liminar judicial para canal 152)
            
            **Idempotência:**
            Envie o header `Idempotency-Key: {codCanal}-{numLote}-{dataGeracao}` para
            garantir que requisições duplicadas retornem o resultado anterior sem reprocessar.
            
            **Processamento parcial:**
            Configurável por canal (FLG_PROCESSAMENTO_PARCIAL). Se ativado, itens válidos
            são processados mesmo que outros sejam rejeitados.
            """
    )

    public ResponseEntity<?> criarPedido(
            @Parameter(description = "Chave de idempotência: {codCanal}-{numLote}-{dataGeracao}")
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateRequestCredit request) {

        // Se não fornecida, gera chave default a partir dos dados do request
        String key = (idempotencyKey != null && !idempotencyKey.isBlank())
                ? idempotencyKey
                : request.codCanal() + "-" + request.numLote() + "-" + request.dataGeracao();

        log.info("Processando lote: canal={} numLote={} dataGeracao={} idempotencyKey={}",
                request.codCanal(), request.numLote(), request.dataGeracao(), key);

        // Verifica se já existe resposta para esta chave
        Optional<ResponseEntity<?>> previous = idempotencyStore.get(key);
        if (previous.isPresent()) {
            log.info("Requisição idempotente detectada. Retornando resposta armazenada para a chave: {}", key);
            return previous.get();
        }

        // Aqui segue o processamento normal do pedido
        // TODO: Substitua pelo processamento real do pedido
        ResponseEntity<?> response = ResponseEntity.status(HttpStatus.CREATED).body("Pedido criado com sucesso");

        // Armazena a resposta para futuras requisições idempotentes
        idempotencyStore.put(key, response);
        return response;
    }

}
