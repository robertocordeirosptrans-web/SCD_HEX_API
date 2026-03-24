package br.sptrans.scd.creditrequest.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.adapter.port.out.jpa.mapper.CreditRequestMapper;
import br.sptrans.scd.creditrequest.application.port.in.dto.CreditRequestDTO;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorCodec;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageRequest;
import br.sptrans.scd.creditrequest.application.port.in.dto.CursorPageResponse;
import br.sptrans.scd.creditrequest.application.port.out.repository.CreditRequestRepository;
import br.sptrans.scd.creditrequest.domain.CreditRequest;
import br.sptrans.scd.creditrequest.domain.SearchModeClassifier;
import br.sptrans.scd.creditrequest.domain.enums.SearchMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para paginação baseada em cursor de CreditRequest.
 * Implementa cursor-based pagination para melhor performance em grandes datasets.
 *
 * <p>Implementa estratégia de busca escalável com três modos de execução:</p>
 * <ul>
 *   <li>SPECIFIC: Busca rápida por identificador único (sem paginação)</li>
 *   <li>OPERATIONAL: Busca padrão com filtros de negócio (com paginação)</li>
 *   <li>ANALYTICAL: Busca complexa com múltiplos filtros (com limites reduzidos)</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CursorPaginationService {

    private final CreditRequestRepository repository;
    private final CreditRequestMapper mapper;
    private final SearchModeClassifier searchModeClassifier;

    /**
     * Busca registros usando paginação por cursor.
     * Performance otimizada para milhões de registros.
     */
    @Cacheable(value = "pedidos", key = "#request.toString()")
    public CursorPageResponse<CreditRequestDTO> findWithCursor(CursorPageRequest request) {
        long startTime = System.currentTimeMillis();

        // Classificar o modo de busca baseado nos filtros
        SearchMode searchMode = searchModeClassifier.classify(request);

        // Validar filtros mínimos para o modo classificado
        searchModeClassifier.validateMinimumFilters(request, searchMode);

        // Validar e ajustar limite baseado no modo
        adjustLimitForSearchMode(request, searchMode);

        log.info("Executando busca: mode={}, filters={}, cursor={}, limit={}",
                searchMode,
                buildFilterSummary(request),
                request.getCursor() != null ? "present" : "null",
                request.getLimit());

        // Executar busca baseado no modo
        CursorPageResponse<CreditRequestDTO> response;
        switch (searchMode) {
            case SPECIFIC:
                response = executeSpecificSearch(request, startTime, searchMode);
                break;
            case OPERATIONAL:
            case ANALYTICAL:
                response = executeOperationalOrAnalyticalSearch(request, startTime, searchMode);
                break;
            default:
                throw new IllegalStateException("Modo de busca desconhecido: " + searchMode);
        }

        long executionTime = System.currentTimeMillis() - startTime;
        log.info("Busca concluída: mode={}, count={}, hasMore={}, executionTimeMs={}",
                searchMode, response.getCount(), response.getHasMore(), executionTime);

        return response;
    }

    /**
     * Executa busca específica (fast path) sem paginação.
     */
    private CursorPageResponse<CreditRequestDTO> executeSpecificSearch(
            CursorPageRequest request, long startTime, SearchMode searchMode) {

        if (request.getCursor() != null) {
            log.warn("Cursor ignorado em busca específica: numSolicitacao={}", request.getNumSolicitacao());
        }

        List<CreditRequest> results = repository.findByNumSolicitacaoSpecific(
                request.getNumSolicitacao(),
                request.getCodCanal());

        List<CreditRequestDTO> dtos = results.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return CursorPageResponse.<CreditRequestDTO>builder()
                .data(dtos)
                .nextCursor(null)
                .previousCursor(null)
                .hasMore(false)
                .count(dtos.size())
                .metadata(CursorPageResponse.PageMetadata.builder()
                        .limit(1)
                        .timestamp(System.currentTimeMillis())
                        .searchMode(searchMode.name())
                        .executionTimeMs(System.currentTimeMillis() - startTime)
                        .build())
                .build();
    }

    /**
     * Executa busca operacional ou analítica com paginação por cursor.
     */
    private CursorPageResponse<CreditRequestDTO> executeOperationalOrAnalyticalSearch(
            CursorPageRequest request, long startTime, SearchMode searchMode) {

        String originalCursor = request.getCursor();

        // Decodificar cursor se fornecido
        CursorCodec.CursorState cursorState = null;
        if (request.getCursor() != null && !request.getCursor().isEmpty()) {
            try {
                cursorState = CursorCodec.decode(request.getCursor());
            } catch (IllegalArgumentException e) {
                log.warn("Cursor inválido recebido: {}", request.getCursor(), e);
                throw new IllegalArgumentException("Cursor inválido ou expirado");
            }
        }

        Long cursorNumSolicitacao = cursorState != null ? cursorState.numSolicitacao() : null;
        String cursorCodCanal = cursorState != null ? cursorState.codCanal() : null;

        List<CreditRequest> results;
        if (request.getCodProduto() != null && !request.getCodProduto().isEmpty()) {
            results = findByCodProduto(request);
        } else {
            int fetchLimit = request.getLimit() + 1;
            results = repository.findWithCursor(
                    cursorNumSolicitacao,
                    cursorCodCanal,
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
                    fetchLimit);
        }

        boolean hasMore = results.size() > request.getLimit();
        if (hasMore) {
            results = results.subList(0, request.getLimit());
        }

        String nextCursor = null;
        if (hasMore && !results.isEmpty()) {
            CreditRequest lastItem = results.get(results.size() - 1);
            nextCursor = CursorCodec.createCursor(
                    lastItem.getNumSolicitacao(),
                    lastItem.getCodCanal());
        }

        List<CreditRequestDTO> dtos = results.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return CursorPageResponse.<CreditRequestDTO>builder()
                .data(dtos)
                .nextCursor(nextCursor)
                .previousCursor(originalCursor)
                .hasMore(hasMore)
                .count(dtos.size())
                .metadata(CursorPageResponse.PageMetadata.builder()
                        .limit(request.getLimit())
                        .timestamp(System.currentTimeMillis())
                        .searchMode(searchMode.name())
                        .executionTimeMs(System.currentTimeMillis() - startTime)
                        .build())
                .build();
    }

    /**
     * Busca específica por código de produto.
     */
    private List<CreditRequest> findByCodProduto(CursorPageRequest request) {
        return repository.findByCodProduto(
                request.getCodProduto(),
                request.getCodCanal(),
                request.getDtInicio(),
                request.getDtFim(),
                request.getLimit() + 1);
    }

    /**
     * Ajusta o limite baseado no modo de busca.
     */
    private void adjustLimitForSearchMode(CursorPageRequest request, SearchMode searchMode) {
        request.validateAndAdjustLimit();

        int maxLimit = searchMode.getMaxPageSize();
        if (request.getLimit() > maxLimit) {
            log.debug("Ajustando limite de {} para {} (modo: {})",
                    request.getLimit(), maxLimit, searchMode);
            request.setLimit(maxLimit);
        }
    }

    /**
     * Constrói resumo dos filtros para logging.
     */
    private String buildFilterSummary(CursorPageRequest request) {
        StringBuilder sb = new StringBuilder(256);

        if (request.getNumSolicitacao() != null) sb.append("numSol=").append(request.getNumSolicitacao()).append(",");
        if (request.getCodCanal() != null) sb.append("canal=").append(request.getCodCanal()).append(",");
        if (request.getCodSituacao() != null) sb.append("situacao=").append(request.getCodSituacao()).append(",");
        if (request.getNumLote() != null) sb.append("lote=").append(request.getNumLote()).append(",");
        if (request.getCodProduto() != null) sb.append("produto=").append(request.getCodProduto()).append(",");
        if (request.getDtInicio() != null) sb.append("dtInicio=").append(request.getDtInicio()).append(",");
        if (request.getDtFim() != null) sb.append("dtFim=").append(request.getDtFim()).append(",");
        if (request.getVlTotalMin() != null) sb.append("vlMin=").append(request.getVlTotalMin()).append(",");
        if (request.getVlTotalMax() != null) sb.append("vlMax=").append(request.getVlTotalMax()).append(",");

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }

        return "no-filters";
    }
}
