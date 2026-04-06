package br.sptrans.scd.product.application.port.out.repository;

import java.util.Optional;

import br.sptrans.scd.product.domain.ChannelFee;
import br.sptrans.scd.product.domain.ChannelFeeKey;

public interface ChannelFeePort {
    Optional<ChannelFee> findById(ChannelFeeKey id);
    ChannelFee save(ChannelFee taxasScanal);
    boolean existsByKey(ChannelFeeKey id);
}
