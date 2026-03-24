package br.sptrans.scd.creditrequest.adapter.port.in.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase;
import br.sptrans.scd.creditrequest.application.port.in.CreditRequestManagementUseCase.SearchCommand;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageRequest;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageResponse;
import br.sptrans.scd.creditrequest.application.service.CreditRequestMapper;
import br.sptrans.scd.shared.version.ApiVersionConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConfig.API_V1_PATH + "/pedidos")
@Tag(name = "Pedidos", description = "API para gestão de pedidos de crédito")
public class CreditRequestController {
    
  private final CreditRequestManagementUseCase creditRequestManagementUseCase;
  private final CreditRequestMapper creditRequestMapper;

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




}
