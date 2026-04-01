package br.sptrans.scd.product.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.sptrans.scd.product.domain.Fare;
import br.sptrans.scd.product.domain.Fee;

public interface FeeFareManagementUseCase {

    // =========================================================================
    // Gestão de Tarifas (Fare)
    // =========================================================================

    /**
     * Cadastra uma nova tarifa para um produto/versão/canal com datas de vigência.
     */
    Fare createFare(RegisterFareCommand command);

    /**
     * Atualiza uma tarifa existente. Somente prorroga a vigência da tarifa.
     */
    Fare updateFare(String codTarifa, UpdateFareCommand command);

    /**
     * Lista tarifas de um produto/canal com filtro de vigência.
     */
    List<Fare> listFares(String codProduto, String codCanal);

    // =========================================================================
    // Gestão de Taxas (Fee)
    // =========================================================================

    /**
     * Cadastra uma nova taxa para um produto/canal, com suas faixas
     * administrativa, de serviço e, opcionalmente, de destino.
     */
    Fee createFee(RegisterFeeCommand command);

    /**
     * Atualiza uma taxa existente. Somente prorroga a vigência da taxa.
     */
    Fee updateFee(Long codTaxa, UpdateFeeCommand command);

    /**
     * Lista taxas de um produto/canal.
     */
    List<Fee> listFees(String codProduto, String codCanal);

    // =========================================================================
    // Records — Tarifas
    // =========================================================================

    record RegisterFareCommand(
            String codProduto,
            String codVersao,
            String codCanal,
            String desTarifa,
            BigDecimal valTarifa,
            LocalDateTime dtInicio,
            LocalDateTime dtFim,
            Long idUsuario) {
    }

    record UpdateFareCommand(
            String desTarifa,
            LocalDateTime dtFim, // apenas a data final pode ser alterada em tarifas vigentes
            Long idUsuario) {
    }

    // =========================================================================
    // Records — Taxas
    // =========================================================================

    record RegisterFeeCommand(
            String codProduto,
            String codCanal,
            String desTaxa,
            LocalDateTime dtInicio,
            LocalDateTime dtFim,
            RegisterAdministrativeFeeCommand taxaAdministrativa,
            RegisterServiceFeeCommand taxaServico,
            RegisterDestinyFeeCommand taxaDestino) { // nullable — opcional
    }

    record UpdateFeeCommand(
            String desTaxa,
            LocalDateTime dtFim, // apenas a data final pode ser alterada em taxas vigentes
            RegisterAdministrativeFeeCommand taxaAdministrativa,
            RegisterServiceFeeCommand taxaServico) {
    }

    // =========================================================================
    // Records — Faixas de taxa
    // =========================================================================

    record RegisterAdministrativeFeeCommand(
            BigDecimal recInicial,
            BigDecimal recFinal,
            BigDecimal valFixo,
            BigDecimal valPercentual) {
    }

    record RegisterServiceFeeCommand(
            BigDecimal recInicial,
            BigDecimal recFinal,
            BigDecimal valFixo,
            BigDecimal valPercentual,
            BigDecimal valMinimo) {
    }

    record RegisterDestinyFeeCommand(
            String codCanalDestino) {
    }

    List<Fee> findByCanalProduto(String codCanal, String codProduto);
}
