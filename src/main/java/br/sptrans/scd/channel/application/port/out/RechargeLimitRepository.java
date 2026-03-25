package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.RechargeLimit;
import br.sptrans.scd.channel.domain.RechargeLimitKey;

public interface RechargeLimitRepository {

    Optional<RechargeLimit> findById(RechargeLimitKey id);

    Optional<RechargeLimit> findByIdOtimized(String codCanal, String codProduto);

    List<RechargeLimit> findAll();

    List<RechargeLimit> findByCodCanal(String codCanal);

    List<RechargeLimit> findByCodProduto(String codProduto);

    RechargeLimit save(RechargeLimit entity);

    void deleteById(RechargeLimitKey id);

    boolean existsById(RechargeLimitKey id);
}
