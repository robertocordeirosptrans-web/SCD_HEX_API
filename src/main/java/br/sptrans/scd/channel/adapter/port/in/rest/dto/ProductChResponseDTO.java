package br.sptrans.scd.channel.adapter.port.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

public record ProductChResponseDTO(
        // Informações básicas do Produto
        String codProduto,
        String desProduto,
        String statusProduto,
        // Informações do Canal
        Integer codCanal,
        String statusCanalProduto,
        // Informações do Canal-Produto
        Integer codConvenio,
        Integer codOrgaoEmissor,
        Integer qtdLimiteComercializacao,
        Integer qtdMinimaEstoque,
        Integer qtdMaximaEstoque,
        Integer qtdMinimaRessuprimento,
        Integer qtdMaximaRessuprimento,
        Integer vlFace,
        Integer tipoOperHM,
        String flgCarac,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        String canaisDestino,
        // Informações da Vigência
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime inicioValidade,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime fimValidade,
        String statusVigencia,
        // Limites de Recarga
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dtInicioValidadeLimite,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dtFimValidadeLimite,
        BigDecimal vlMinimoRecarga,
        BigDecimal vlMaximoRecarga,
        BigDecimal vlMaximoSaldo,
        String statusLimite,
        // Informações de Taxas
        Integer idTaxa,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime taxaInicio,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime taxaFim,
        String dscTaxa,
        // Taxa Administrativa
        BigDecimal taxaAdmRecInicial,
        BigDecimal taxaAdmRecFinal,
        BigDecimal taxaAdmValFixo,
        BigDecimal taxaAdmPercentual,
        // Taxa de Serviço
        BigDecimal taxaServRecInicial,
        BigDecimal taxaServRecFinal,
        BigDecimal taxaServValFixo,
        BigDecimal taxaServPercentual,
        BigDecimal taxaServValMinimo,
        // Taxa por Canal
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime taxaCanalInicio,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime taxaCanalFim,
        BigDecimal taxaCanalVlInicio,
        BigDecimal taxaCanalVlFinal,
        BigDecimal taxaCanalPercentual
        ) {

    public ProductChResponseDTO                                           {
        // Validações opcionais
        if (vlMinimoRecarga == null) {
            vlMinimoRecarga = BigDecimal.ZERO;
        }
        if (vlMaximoRecarga == null) {
            vlMaximoRecarga = BigDecimal.ZERO;
        }
        if (vlMaximoSaldo == null) {
            vlMaximoSaldo = BigDecimal.ZERO;
        }
    }

}
