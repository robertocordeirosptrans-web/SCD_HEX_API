package br.sptrans.scd.channel.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;

public interface RechargeLimitUseCase {

        RechargeLimit createRechargeLimit(CreateRechargeLimitCommand command);

        RechargeLimit updateRechargeLimit(String codCanal, String codProduto, UpdateRechargeLimitCommand command);

        RechargeLimit findRechargeLimit(RechargeLimitKey key);

        Page<RechargeLimit> findAllRechargeLimits(Pageable pageable);

        void deleteRechargeLimit(RechargeLimitKey key);


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
                        User usuario) {
        }

        record UpdateRechargeLimitCommand(
                        LocalDateTime dtInicioValidade,
                        LocalDateTime dtFimValidade,
                        BigDecimal vlMinimoRecarga,
                        BigDecimal vlMaximoRecarga,
                        BigDecimal vlMaximoSaldo,
                        String codStatus,
                        Long idUsuario) {
        }
}
