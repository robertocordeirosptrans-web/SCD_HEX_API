package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateSalesChannelRequest(
            String codCanalSuperior,
            String desCanal,
            String desRazaoSocial,
            String desNomeFantasia,
            BigDecimal vlCaucao,
            LocalDateTime dtInicioCaucao,
            LocalDateTime dtFimCaucao,
            Integer seqNivel,
            String flgCriticaNumlote,
            Integer flgLimiteDias,
            String flgProcessamentoAutomatico,
            String flgProcessamentoParcial,
            String flgSaldoDevedor,
            Integer numMinutoIniLibRecarga,
            Integer numMinutoFimLibRecarga,
            String flgEmiteReciboPedido,
            String flgSupercanal,
            String flgPagtoFuturo,
            String codClassificacaoPessoa,
            String codAtividade) {}
