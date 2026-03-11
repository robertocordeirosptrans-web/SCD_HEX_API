package br.sptrans.scd.creditrequest.application.port.in.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO para paginação baseada em cursor.
 * Suporta filtros extensivos para SolDistribuicoes.
 */
@Data
@Builder
public class CursorPageRequest {

    // Cursor opaco (Base64 encoded)
    private String cursor;

    // Tamanho da página (padrão: 20, máximo: 100)
    @Builder.Default
    private Integer limit = 20;

    // Filtros existentes
    private String codCanal;
    private String codSituacao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtFim;

    // Novos filtros
    private String numLote;
    private Long numSolicitacao;
    private String codProduto;
    private String codLogin;
    private String codFormaPagto;

    // Filtros de valor
    private BigDecimal vlTotalMin;
    private BigDecimal vlTotalMax;

    // Filtros de datas adicionais
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtLiberacaoEfetivaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtLiberacaoEfetivaFim;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtPagtoEconomicaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtPagtoEconomicaFim;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtFinanceiraInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtFinanceiraFim;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtAlteracaoInicio;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dtAlteracaoFim;

    /**
     * Valida e ajusta o limit para estar dentro dos limites permitidos
     */
    public void validateAndAdjustLimit() {
        if (limit == null || limit < 1) {
            limit = 20;
        } else if (limit > 100) {
            limit = 100;
        }
    }
}
