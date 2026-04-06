package br.sptrans.scd.channel.adapter.port.out.jpa.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.sptrans.scd.channel.adapter.port.out.jpa.mapper.ContactChannelMapper;
import br.sptrans.scd.channel.adapter.port.out.jpa.repository.ContactChannelJpaRepository;
import br.sptrans.scd.channel.adapter.port.out.persistence.entity.ContactChannelEntityJpa;
import br.sptrans.scd.channel.application.port.out.ContactChannelPersistencePort;
import br.sptrans.scd.channel.domain.ContactChannel;
import lombok.RequiredArgsConstructor;

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
    public Page<ContactChannel> findAllByCanal(String codCanal, Pageable pageable) {
        return contactChannelJpaRepository.findAllByCodCanal(codCanal, pageable)
                .map(contactChannelMapper::toDomain);
    }

    @Override
    public ContactChannel save(ContactChannel contactChannel) {
        ContactChannelEntityJpa entity = contactChannelMapper.toEntity(contactChannel);
        ContactChannelEntityJpa saved = contactChannelJpaRepository.save(entity);
        return contactChannelMapper.toDomain(saved);
    }





}
