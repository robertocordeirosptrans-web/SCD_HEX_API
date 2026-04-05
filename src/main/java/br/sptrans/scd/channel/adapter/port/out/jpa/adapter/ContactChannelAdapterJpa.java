package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import br.sptrans.scd.channel.application.port.out.ContactChannelPersistencePort;
import br.sptrans.scd.channel.domain.ContactChannel;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.ContactChannelJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.ContactChannelEntityJpa;
import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.ContactChannelMapper;

@Repository
@RequiredArgsConstructor
public class ContactChannelAdapterJpa implements ContactChannelPersistencePort {

    private final ContactChannelJpaRepository contactChannelJpaRepository;
    private final ContactChannelMapper contactChannelMapper;

    @Override
    public Optional<ContactChannel> findById(String codContato) {
        return contactChannelJpaRepository.findByCodContato(codContato)
                .map(contactChannelMapper::toDomain);
    }

    @Override
    public void deleteById(String codContato) {
        contactChannelJpaRepository.deleteById(codContato);
    }

    @Override
    public boolean existsById(String codContato) {
        return contactChannelJpaRepository.existsByCodContato(codContato);
    }

    @Override
    public List<ContactChannel> findAllByCanal(String codCanal) {
        List<ContactChannelEntityJpa> entities = contactChannelJpaRepository.findAllByCodCanal(codCanal);
        return entities.stream().map(contactChannelMapper::toDomain).toList();
    }

    @Override
    public ContactChannel save(ContactChannel contactChannel) {
        ContactChannelEntityJpa entity = contactChannelMapper.toEntity(contactChannel);
        ContactChannelEntityJpa saved = contactChannelJpaRepository.save(entity);
        return contactChannelMapper.toDomain(saved);
    }





}
