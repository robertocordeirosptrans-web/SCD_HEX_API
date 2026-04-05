package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.sptrans.scd.auth.domain.User;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.AddressChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.SalesChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.AddressChannelJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.SalesChannelJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.AddressChannelEntityJpa;
import br.sptrans.scd.auth.application.port.out.UserQueryPort;
import br.sptrans.scd.channel.application.port.out.AddressChannelPersistencePort;

import br.sptrans.scd.channel.domain.AddressChannel;
import br.sptrans.scd.channel.domain.SalesChannel;
import br.sptrans.scd.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class AddressChannelAdapterJpa implements AddressChannelPersistencePort {
    private final AddressChannelJpaRepository repository;
    private final AddressChannelMapper addressChannelMapper;
    private final SalesChannelJpaRepository salesChannelJpaRepository;
    private final SalesChannelMapper salesChannelMapper;
    private final UserQueryPort userQueryPort;

    private User resolveUser(Long id) {
        if (id == null) return null;
        return userQueryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
    }

    private SalesChannel resolveSalesChannel(String codCanal) {
        if (codCanal == null) return null;
        return salesChannelJpaRepository.findByCodCanal(codCanal)
                .map(salesChannelMapper::toDomain)
                .orElse(null);
    }

    private AddressChannel toDomain(AddressChannelEntityJpa entity) {
        SalesChannel salesChannel = resolveSalesChannel(entity.getCodCanal());
        User userCad = resolveUser(entity.getIdUsuarioCadastro());
        User userMan = resolveUser(entity.getIdUsuarioManutencao());
        return addressChannelMapper.toDomain(entity, salesChannel, userCad, userMan);
    }

    @Override
    public Optional<AddressChannel> findById(String codEndereco) {
        return repository.findByCodEndereco(codEndereco)
                .map(this::toDomain);
    }

    @Override
    public boolean existsById(String codEndereco) {
        return repository.existsByCodEndereco(codEndereco);
    }

    @Override
    public List<AddressChannel> findAllByCanal(String codCanal) {
        if (codCanal != null && !codCanal.isBlank()) {
            return repository.findAllByCodCanal(codCanal).stream().map(this::toDomain).toList();
        }
        return repository.findAllOrderByCodEndereco().stream().map(this::toDomain).toList();
    }

    @Override
    public AddressChannel save(AddressChannel ac) {
        AddressChannelEntityJpa entity = addressChannelMapper.toEntity(ac);
        AddressChannelEntityJpa saved = repository.save(entity);
        return addressChannelMapper.toDomain(saved, ac.getCodCanal(), ac.getIdUsuarioCadastro(), ac.getIdUsuarioManutencao());
    }

    @Override
    public void deleteById(String codEndereco) {
        repository.deleteById(codEndereco);
    }
}
