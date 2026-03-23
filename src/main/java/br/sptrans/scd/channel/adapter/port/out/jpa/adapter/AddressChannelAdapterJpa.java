package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.entity.AddressChannelEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.AddressChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.AddressChannelJpaRepository;
import br.sptrans.scd.channel.application.port.out.AddressChannelRepository;
import br.sptrans.scd.channel.domain.AddressChannel;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class AddressChannelAdapterJpa implements AddressChannelRepository {
    private final AddressChannelJpaRepository repository;
    private final AddressChannelMapper addressChannelMapper;

    @Override
    public Optional<AddressChannel> findById(String codEndereco) {
        return repository.findByCodEndereco(codEndereco)
                .map(addressChannelMapper::toDomain);
    }

    @Override
    public boolean existsById(String codEndereco) {
        return repository.existsByCodEndereco(codEndereco);
    }

    @Override
    public List<AddressChannel> findAllByCanal(String codCanal) {
        if (codCanal != null && !codCanal.isBlank()) {
            return repository.findAllByCodCanal(codCanal).stream().map(addressChannelMapper::toDomain).toList();
        }
        return repository.findAllOrderByCodEndereco().stream().map(addressChannelMapper::toDomain).toList();
    }

    @Override
    public AddressChannel save(AddressChannel ac) {
        AddressChannelEntityJpa entity = addressChannelMapper.toEntity(ac);
        AddressChannelEntityJpa saved = repository.save(entity);
        return addressChannelMapper.toDomain(saved);
    }

    @Override
    public void deleteById(String codEndereco) {
        repository.deleteById(codEndereco);
    }


}
