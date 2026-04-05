package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.auth.domain.User;

public interface SalesChannelRepository {

    Optional<SalesChannel> findById(String codCanal);

    boolean existsById(String codCanal);

    List<SalesChannel> findAll(String stCanais);

    SalesChannel save(SalesChannel salesChannel);

    void updateStatus(String codCanal, String stCanais, User usuario);

    void deleteById(String codCanal);

    List<SalesChannel> findByCodCanalSuperior(String codCanalSuperior);
}
