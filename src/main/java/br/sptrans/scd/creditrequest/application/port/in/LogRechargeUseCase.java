package br.sptrans.scd.creditrequest.application.port.in;

import java.time.LocalDateTime;

public interface LogRechargeUseCase {

    int upsertLogRecarga(String numLogicoCartao,
            Long idUsuarioCadastro,
            LocalDateTime dtSolicRecarga,
            LocalDateTime dtCadastro,
            LocalDateTime dtManutencao,
            Long idUsuarioManutencao);

}
