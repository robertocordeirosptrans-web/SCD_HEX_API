package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;


import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.SalesChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.SalesChannelJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.SalesChannelEntityJpa;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.application.port.out.SalesChannelRepository;
import br.sptrans.scd.channel.domain.SalesChannel;
import lombok.RequiredArgsConstructor;
import br.sptrans.scd.auth.domain.User;



@Repository
@RequiredArgsConstructor
public class SalesChannelAdapterJpa implements SalesChannelRepository {

    private final SalesChannelJpaRepository repository;
    private final SalesChannelMapper mapper;

    @Override
    public Optional<SalesChannel> findById(String codCanal) {
        return repository.findByCodCanal(codCanal)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(String codCanal) {
        return repository.existsByCodCanal(codCanal);
    }

    @Override
    public List<SalesChannel> findAll(String stCanais) {
        List<SalesChannelEntityJpa> entities = repository.findAllByStCanais(stCanais);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public SalesChannel save(SalesChannel sc) {
        SalesChannelEntityJpa entity = mapper.toEntity(sc);
        SalesChannelEntityJpa saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void updateStatus(String codCanal, String stCanais, User usuario) {
        Long idUsuario = usuario != null ? usuario.getIdUsuario() : null;
        repository.updateStatus(stCanais, idUsuario, codCanal);
    }

    @Override
    public void deleteById(String codCanal) {
        repository.deleteById(codCanal);
    }

    @Override
    public List<SalesChannel> findByCodCanalSuperior(String codCanalSuperior) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByCodCanalSuperior'");
    }

}
