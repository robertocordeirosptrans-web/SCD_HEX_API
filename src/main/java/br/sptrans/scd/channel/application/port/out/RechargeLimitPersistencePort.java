package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;

public interface RechargeLimitPersistencePort {
    Optional<RechargeLimit> findById(RechargeLimitKey id);

    List<RechargeLimit> findAll();

    boolean existsById(RechargeLimitKey id);

    RechargeLimit save(RechargeLimit entity);
}
