package br.sptrans.scd.channel.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.sptrans.scd.channel.domain.RechargeLimit;

public interface RechargeLimitUseCase {

    RechargeLimit createRechargeLimit(CreateRechargeLimitCommand command);

    RechargeLimit updateRechargeLimit(String codCanal, String codProduto, UpdateRechargeLimitCommand command);

    RechargeLimit findRechargeLimit(String codCanal, String codProduto);

    List<RechargeLimit> findAllRechargeLimits();

    List<RechargeLimit> findByCodCanal(String codCanal);

    List<RechargeLimit> findByCodProduto(String codProduto);

    void deleteRechargeLimit(String codCanal, String codProduto);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateRechargeLimitCommand(
            String codCanal,
            String codProduto,
            LocalDateTime dtInicioValidade,
            LocalDateTime dtFimValidade,
            BigDecimal vlMinimoRecarga,
            BigDecimal vlMaximoRecarga,
            BigDecimal vlMaximoSaldo,
            String codStatus,
            Long idUsuario) {}

    record UpdateRechargeLimitCommand(
            LocalDateTime dtInicioValidade,
            LocalDateTime dtFimValidade,
            BigDecimal vlMinimoRecarga,
            BigDecimal vlMaximoRecarga,
            BigDecimal vlMaximoSaldo,
            String codStatus,
            Long idUsuario) {}
}
