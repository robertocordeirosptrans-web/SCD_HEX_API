package br.sptrans.scd.creditrequest.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.sptrans.scd.creditrequest.application.port.in.LogRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.RechargeLogRepository;
import br.sptrans.scd.creditrequest.domain.RechargeLog;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RechargeLogService implements LogRechargeUseCase {
    private final RechargeLogRepository rechargeLogRepository;

    private static final int SEQ_RECARGA_INICIAL = 0;

    @Override
    public int upsertLogRecarga(LogRechargeCommand command) {
        Optional<RechargeLog> existingOpt = rechargeLogRepository.findByNumLogicoCartao(command.numLogicoCartao());
        if (existingOpt.isPresent()) {
            RechargeLog existing = existingOpt.get();
            int seq = existing.getSeqRecarga() != null ? existing.getSeqRecarga() : SEQ_RECARGA_INICIAL;
            existing.setIdUsuarioCadastro(command.idUsuarioCadastro());
            existing.setDtSolicRecarga(command.dtSolicRecarga());
            existing.setDtCadastro(command.dtCadastro());
            existing.setDtManutencao(command.dtManutencao());
            existing.setIdUsuarioManutencao(null);
            rechargeLogRepository.save(existing);
            return seq;
        } else {
            RechargeLog novo = new RechargeLog();
            novo.setNumLogicoCartao(command.numLogicoCartao());
            novo.setSeqRecarga(SEQ_RECARGA_INICIAL);
            novo.setIdUsuarioCadastro(command.idUsuarioCadastro());
            novo.setDtSolicRecarga(command.dtSolicRecarga());
            novo.setDtCadastro(command.dtCadastro());
            novo.setDtManutencao(command.dtManutencao());
            novo.setIdUsuarioManutencao(command.idUsuarioManutencao());
            rechargeLogRepository.save(novo);
            return SEQ_RECARGA_INICIAL;
        }
    }
}
