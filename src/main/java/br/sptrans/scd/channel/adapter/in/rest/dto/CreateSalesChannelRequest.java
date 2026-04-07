package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSalesChannelRequest(
    @NotBlank @Size(max = 20) String codCanal,
    @Size(max = 20) String codDocumento,
    @Size(max = 20) String codCanalSuperior,
    @NotBlank @Size(max = 60) String desCanal,
    @Size(max = 4) String codTipoDocumento,
    @Size(max = 60) String desRazaoSocial,
    @Size(max = 60) String desNomeFantasia,
    @NotNull BigDecimal vlCaucao,
    LocalDateTime dtInicioCaucao,
    LocalDateTime dtFimCaucao,
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