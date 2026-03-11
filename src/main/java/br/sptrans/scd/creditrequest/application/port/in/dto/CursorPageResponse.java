package br.sptrans.scd.creditrequest.application.port.in.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO para paginação baseada em cursor.
 * Retorna dados paginados com cursor opaco para próxima página.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CursorPageResponse<T> {

    /**
     * Lista de itens da página atual
     */
    private List<T> data;

    /**
     * Cursor opaco para a próxima página (null se não houver mais páginas)
     */
    private String nextCursor;

    /**
     * Cursor opaco para a página anterior (para navegação bidirecional)
     */
    private String previousCursor;

    /**
     * Indica se há mais páginas disponíveis
     */
    private Boolean hasMore;

    /**
     * Número de itens retornados nesta página
     */
    private Integer count;

    /**
     * Metadados adicionais (opcional)
     */
    private PageMetadata metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageMetadata {
        /**
         * Limite de itens por página usado
         */
        private Integer limit;

        /**
         * Timestamp da consulta
         */
        private Long timestamp;

        /**
         * Filtros aplicados (opcional, para debug)
         */
        private Object appliedFilters;

        /**
         * Modo de busca utilizado (SPECIFIC, OPERATIONAL, ANALYTICAL)
         */
        private String searchMode;

        /**
         * Tempo de execução da query em milissegundos
         */
        private Long executionTimeMs;
    }
}
