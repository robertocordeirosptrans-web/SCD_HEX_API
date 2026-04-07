package br.sptrans.scd.channel.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RechargeLimitResponseDTO(
        String codCanal,
        String codProduto,
        LocalDateTime dtInicioValidade,
        LocalDateTime dtFimValidade,
        BigDecimal vlMinimoRecarga,
        BigDecimal vlMaximoRecarga,
        BigDecimal vlMaximoSaldo,
        String codStatus,
        LocalDateTime dtManutencao,
        // Usuário detalhado
        UserSimpleDTO usuarioCadastroInfo
) {
}
