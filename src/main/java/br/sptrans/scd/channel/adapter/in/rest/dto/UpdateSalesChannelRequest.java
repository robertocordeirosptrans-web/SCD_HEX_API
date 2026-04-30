package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSalesChannelRequest(
    @Size(max = 20) String codCanalSuperior,
    @NotBlank @Size(max = 60) String desCanal,
    @Size(max = 60) String desRazaoSocial,
    @Size(max = 60) String desNomeFantasia,
    BigDecimal vlCaucao,
    @Nullable LocalDate dtInicioCaucao,
    @Nullable LocalDate dtFimCaucao,
    Integer seqNivel,
    @Size(max = 1) String flgCriticaNumlote,
    Integer flgLimiteDias,
    @Size(max = 1) String flgProcessamentoAutomatico,
    @Size(max = 1) String flgProcessamentoParcial,
    @Size(max = 1) String flgSaldoDevedor,
    Integer numMinutoIniLibRecarga,
    Integer numMinutoFimLibRecarga,
    @Size(max = 1) String flgEmiteReciboPedido,
    @Size(max = 1) String flgSupercanal,
    @Size(max = 1) String flgPagtoFuturo,
    @Size(max = 20) String codClassificacaoPessoa,
    @Size(max = 20) String codAtividade
) {}
