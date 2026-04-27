package br.sptrans.scd.creditrequest.adapter.in.rest;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.auth.application.port.out.UserPersistencePort;
import br.sptrans.scd.creditrequest.adapter.in.rest.response.ListarItensResponse;
import br.sptrans.scd.creditrequest.adapter.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.BlockCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.CancelCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.OrderItemEntry;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.PayCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.PayItemEntry;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.SearchCommand;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.UnblockCommand;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageRequest;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageResponse;
import br.sptrans.scd.creditrequest.application.port.in.dto.UpdateRequestCredit;
import br.sptrans.scd.creditrequest.application.port.out.projection.ProductPeriodReportProjection;
import br.sptrans.scd.creditrequest.domain.CreditRequestItems;
import br.sptrans.scd.shared.dto.PageResponse;
import br.sptrans.scd.shared.helper.UserResolverHelper;
import br.sptrans.scd.shared.idempotency.IdempotencyStore;
import br.sptrans.scd.shared.idempotency.InMemoryIdempotencyStore;
import br.sptrans.scd.shared.security.CreditRequestPermissions;
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
        private final UserPersistencePort userRepository;

        private final UserResolverHelper userResolverHelper;

        private static final IdempotencyStore<ResponseEntity<?>> idempotencyStore = new InMemoryIdempotencyStore<>();

        @GetMapping("/{id}/itens")
        public ResponseEntity<PageResponse<ListarItensResponse>> listarItens(
                @Parameter(description = "numSolicitacaoItem é obrigatório", required = true)
                @PathVariable Long id,
                @Parameter(description = "Código do canal", required = true) @RequestParam String canal,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "20") int size) {

            PageRequest pageRequest = PageRequest.of(page, size);
            Page<CreditRequestItems> itemsPage = creditRequestManagementUseCase.searchOrderByChannel(canal, id, pageRequest);
            Page<ListarItensResponse> responsePage = itemsPage.map(ListarItensResponse::fromDomain);
            PageResponse<ListarItensResponse> pageResponse = PageResponse.fromPage(responsePage);
            return ResponseEntity.ok(pageResponse);
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
            return value != null ? Long.valueOf(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }

        @GetMapping("/period")
        @Operation(summary = "Gera relatório de período de produto", description = """
                        Gera um relatório completo mostrando a contagem diária de itens por produto para um período específico.

                        **Recursos:**

                        - Detalhamento diário da contagem de itens (até 31 dias)
                        - Agregado por código de produto
                        - Inclui totais financeiros (VL_TOTAL e VL_PAGO)
                        - Filtros por código de canal e intervalo de datas
                        - Filtro opcional por códigos de produto específicos

                        **Restrições:**

                        - O código de canal é obrigatório
                        - O intervalo de datas não pode exceder 31 dias
                        - A data de início deve ser anterior ou igual à data de término

                        **Estrutura da resposta:**

                        Cada registro contém:

                        - codProduto: Código do produto
                        - dia01 a dia31: Contagem de itens para cada dia (a partir de dataInicio)
                        - totalItensPeriodo: Total de itens em todos os dias
                        - vlTotalPeriodo: Valor financeiro total
                        - vlPagoPeriodo: Valor total pago
                                    """)
        public ResponseEntity<List<ProductPeriodReportProjection>> getProductPeriodReport(
                        @Parameter(description = "Código do Canal", required = true) @RequestParam String codCanal,
                        @Parameter(description = "Data de início (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                        @Parameter(description = "Data de término (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                        @Parameter(description = "Lista opcional de códigos de produto para filtrar (separados por vírgula)", required = false) @RequestParam(required = false) List<String> listCodProdutos) {
                List<ProductPeriodReportProjection> report = creditRequestManagementUseCase
                                .generateProductPeriodReport(
                                                new CreditRequestManagementUseCase.ProductPeriodReportEntry(codCanal,
                                                                dataInicio.atStartOfDay(), dataFim.atStartOfDay(),
                                                                listCodProdutos));
                return ResponseEntity.ok(report);
        }

        /**
         * Busca pedidos com paginação por cursor. Classificação automática do modo
         * de busca.
         */
        @PostMapping("/buscar")
        @ResponseBody
        @Operation(summary = "Listar pedidos com cursor-based pagination e modos de busca dinâmicos", description = """
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
                        """)
        public CursorPageResponse<CreditRequestDTO> buscarPedidos(@RequestBody CursorPageRequest request) {
                // Permissão: BUSCAR
                if (!hasAuthority(CreditRequestPermissions.BUSCAR)) {
                        throw new org.springframework.security.access.AccessDeniedException("Acesso negado: BUSCAR");
                }
                // Se não tiver permissão especial, força codCanal do usuário
                if (!hasSobreCanalPermission()) {
                        setCodCanalSafe(request, userResolverHelper.getCurrentCodEmpresa());
                }

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

        @PostMapping("/alterar-status")
        @ResponseBody
        @Operation(summary = "Alterar status de pedidos de crédito", description = "Altera o status dos pedidos conforme ação informada.")
        public void alterarStatus(@RequestBody UpdateRequestCredit request) {
                // Permissões por ação
                switch (request.getAcao()) {
                        case BLOQUEAR -> {
                                if (!hasAuthority(CreditRequestPermissions.ALTERAR_STATUS_BLOQ)) {
                                        throw new org.springframework.security.access.AccessDeniedException(
                                                        "Acesso negado: BLOQUEAR");
                                }
                        }
                        case CANCELAR -> {
                                if (!hasAuthority(CreditRequestPermissions.ALTERAR_STATUS_CANC)) {
                                        throw new org.springframework.security.access.AccessDeniedException(
                                                        "Acesso negado: CANCELAR");
                                }
                        }
                        case PAGO -> {
                                if (!hasAuthority(CreditRequestPermissions.ALTERAR_STATUS_PAGO)) {
                                        throw new org.springframework.security.access.AccessDeniedException(
                                                        "Acesso negado: PAGO");
                                }
                        }
                        default -> throw new IllegalArgumentException("Unexpected value: " + request.getAcao());
                        
                }
                // Se não tiver permissão especial, força codCanal do usuário
                if (!hasSobreCanalPermission()) {
                        setCodCanalSafe(request, userResolverHelper.getCurrentCodEmpresa());
                }
                switch (request.getAcao()) {
                        case BLOQUEAR ->
                                creditRequestManagementUseCase.block(
                                                new BlockCommand(toEntries(request), null, request.getObservacao()));
                        case DESBLOQUEAR ->
                                creditRequestManagementUseCase.unblock(
                                                new UnblockCommand(toEntries(request), null, request.getObservacao()));
                        case CANCELAR ->
                                creditRequestManagementUseCase.cancel(
                                                new CancelCommand(toEntries(request), null, request.getObservacao()));
                        case PAGO ->
                                creditRequestManagementUseCase.pay(mapToPayCommand(request));
                        default ->
                                throw new IllegalArgumentException("Ação não suportada: " + request.getAcao());
                }
        }

        private List<OrderItemEntry> toEntries(UpdateRequestCredit req) {
                return req.getPedidosPermitidos().stream()
                                .map(item -> new OrderItemEntry(
                                                item.getNumSolicitacao(),
                                                req.getCodCanal(),
                                                item.getItens() != null
                                                                ? item.getItens().stream()
                                                                                .map(UpdateRequestCredit.ItemDetail::getNumSolicitacaoItem)
                                                                                .toList()
                                                                : List.of()))
                                .toList();
        }

        private PayCommand mapToPayCommand(UpdateRequestCredit req) {
                return new PayCommand(
                                req.getPedidosPermitidos().stream()
                                                .flatMap(item -> item.getItens().stream()
                                                                .map(detail -> new PayItemEntry(
                                                                                item.getNumSolicitacao(),
                                                                                detail.getNumSolicitacaoItem(),
                                                                                req.getCodCanal(),
                                                                                detail.getCodProduto(),
                                                                                detail.getCodSituacao(),
                                                                                detail.getVlItem(),
                                                                                detail.getVlTxadm(),
                                                                                detail.getVlTxserv())))
                                                .toList(),
                                null,
                                req.getCodFormaPagto(),
                                req.getVlPago(),
                                null);
        }

        @PostMapping("create")
        @Operation(summary = "Criar pedidos de crédito em lote", description = """
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
                        """)

        public ResponseEntity<?> criarPedido(
                        @Parameter(description = "Chave de idempotência: {codCanal}-{numLote}-{dataGeracao}") @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                        @Valid @RequestBody CreateRequestCredit request) {

                // Permissão: CRIAR
                if (!hasAuthority(CreditRequestPermissions.CRIAR)) {
                        throw new org.springframework.security.access.AccessDeniedException("Acesso negado: CRIAR");
                }

                // Se não tiver permissão especial, força codCanal do usuário
                if (!hasSobreCanalPermission()) {
                        setCodCanalSafe(request, userResolverHelper.getCurrentCodEmpresa());
                }

                // Recupera o login do usuário autenticado
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String login = (authentication != null) ? (String) authentication.getPrincipal() : null;
                if (login == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado");
                }

                // Busca o usuário e obtém o idUsuario
                var userOpt = userRepository.findByCodLogin(login);
                if (userOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
                }
                Long userId = userOpt.get().getIdUsuario();

                // Chama o caso de uso para criar o pedido de crédito
                var result = creditRequestManagementUseCase.createCreditRequest(request, idempotencyKey, userId);
                ResponseEntity<?> response = ResponseEntity.status(HttpStatus.CREATED).body(result);

                idempotencyStore.put(idempotencyKey, response);
                return response;

        }

        /**
         * Verifica se o usuário autenticado possui a permissão informada.
         */
        private boolean hasAuthority(String authority) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null)
                        return false;
                return authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .anyMatch(auth -> auth.equals(authority));
        }

        /**
         * Define o codCanal no request, se o método existir.
         */
        private void setCodCanalSafe(Object request, String codCanal) {
                try {
                        var method = request.getClass().getMethod("setCodCanal", String.class);
                        method.invoke(request, codCanal);
                } catch (NoSuchMethodException e) {
                        // método não existe, ignora
                } catch (IllegalAccessException | SecurityException | InvocationTargetException e) {
                        throw new RuntimeException("Erro ao definir codCanal", e);
                }
        }

        /**
         * Verifica se o usuário autenticado possui a permissão de sobrepor canal.
         */

        private boolean hasSobreCanalPermission() {
                return hasAuthority(CreditRequestPermissions.SOBRE_CANAL);
        }

        private static Long parseLongOrNull(String value) {
                try {
                        return value != null ? Long.valueOf(value) : null;
                } catch (NumberFormatException e) {
                        return null;
                }
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }

}
