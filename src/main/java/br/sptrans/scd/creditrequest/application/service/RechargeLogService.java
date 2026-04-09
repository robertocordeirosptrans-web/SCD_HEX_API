package br.sptrans.scd.creditrequest.application.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sptrans.scd.creditrequest.application.port.in.LogRechargeUseCase;
import br.sptrans.scd.creditrequest.application.port.out.repository.RechargeLogPort;
import br.sptrans.scd.creditrequest.domain.RechargeLog;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RechargeLogService implements LogRechargeUseCase {

    private final RechargeLogPort rechargeLogRepository;

    @Override
    public int upsertLogRecarga(String numLogicoCartao,
            Long idUsuarioCadastro,
            LocalDateTime dtSolicRecarga,
            LocalDateTime dtCadastro,
            LocalDateTime dtManutencao,
            Long idUsuarioManutencao) {


        int baseSeq = rechargeLogRepository.findMaxSeqRecarga().orElse(1);
        AtomicInteger seqCounter = new AtomicInteger(baseSeq);

        Optional<RechargeLog> existingOpt = rechargeLogRepository.findByNumLogicoCartao(numLogicoCartao);

        if (existingOpt.isPresent()) {
            // UPDATE — atualiza todos os campos E gera novo SEQ
            RechargeLog existing = existingOpt.get();
            existing.setSeqRecarga(seqCounter.incrementAndGet()); // ← novo SEQ
            existing.setIdUsuarioCadastro(idUsuarioCadastro);
            existing.setDtSolicRecarga(dtSolicRecarga);
            existing.setDtCadastro(LocalDateTime.now());
            existing.setDtManutencao(LocalDateTime.now());
            existing.setIdUsuarioManutencao(null); // ← NULL conforme deduzido
            rechargeLogRepository.save(existing);

        } else {
            // INSERT — cartão ainda não existe em LOG_RECARGAS
            RechargeLog novo = new RechargeLog();
            novo.setNumLogicoCartao(numLogicoCartao);
            novo.setSeqRecarga(seqCounter.incrementAndGet()); // ← novo SEQ único
            novo.setIdUsuarioCadastro(idUsuarioCadastro);
            novo.setDtSolicRecarga(dtSolicRecarga);
            novo.setDtCadastro(LocalDateTime.now());
            novo.setDtManutencao(LocalDateTime.now());
            novo.setIdUsuarioManutencao(idUsuarioManutencao);
            rechargeLogRepository.save(novo);
        }

        return seqCounter.get(); // ← sempre retorna o SEQ recém gerado
    }
}
