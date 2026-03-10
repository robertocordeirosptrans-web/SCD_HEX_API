package br.sptrans.scd.channel.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.sptrans.scd.channel.domain.SalesChannel;

public interface SalesChannelUseCase {

    SalesChannel createSalesChannel(CreateSalesChannelCommand command);

    SalesChannel updateSalesChannel(String codCanal, UpdateSalesChannelCommand command);

    SalesChannel findBySalesChannel(String codCanal);

    List<SalesChannel> findAllSalesChannels(String stCanais);

    void activateSalesChannel(String codCanal, Long idUsuario);

    void inactivateSalesChannel(String codCanal, Long idUsuario);

    void deleteSalesChannel(String codCanal);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateSalesChannelCommand(
            String codCanal,
            String codDocumento,
            String codCanalSuperior,
            String desCanal,
            String codTipoDocumento,
            String desRazaoSocial,
            String desNomeFantasia,
            BigDecimal vlCaucao,
            LocalDate dtInicioCaucao,
            LocalDate dtFimCaucao,
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
            String codAtividade,
            Long idUsuario) {}

    record UpdateSalesChannelCommand(
            String codCanalSuperior,
            String desCanal,
            String desRazaoSocial,
            String desNomeFantasia,
            BigDecimal vlCaucao,
            LocalDate dtInicioCaucao,
            LocalDate dtFimCaucao,
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
            String codAtividade,
            Long idUsuario) {}
}
