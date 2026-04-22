package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import br.sptrans.scd.channel.adapter.out.persistence.entity.SalesChannelEntityJpa;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.in.rest.dto.SubSalesChannelProjection;
import br.sptrans.scd.channel.domain.SalesChannel;

public interface SalesChannelPersistencePort {
    
    Optional<SalesChannel> findById(String codCanal);

    boolean existsById(String codCanal);

    Page<SalesChannel> findAll(Specification<SalesChannelEntityJpa> spec, Pageable pageable);

    SalesChannel save(SalesChannel salesChannel);

    void updateStatus(String codCanal, String stCanais, User usuario);

    void deleteById(String codCanal);

    List<SalesChannel> findByCodCanalSuperior(String codCanalSuperior);

    Page<SubSalesChannelProjection> findSubChannelsByCodCanalSuperior(String codCanalSuperior, Pageable pageable);
}
