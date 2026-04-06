package br.sptrans.scd.channel.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;

public interface RechargeLimitPersistencePort {
    Optional<RechargeLimit> findById(RechargeLimitKey id);

    Page<RechargeLimit> findAll(Pageable pageable);

    boolean existsById(RechargeLimitKey id);

    RechargeLimit save(RechargeLimit entity);
}
