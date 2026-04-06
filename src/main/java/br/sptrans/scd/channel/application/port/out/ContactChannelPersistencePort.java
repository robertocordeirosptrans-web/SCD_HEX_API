package br.sptrans.scd.channel.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.domain.ContactChannel;

public interface ContactChannelPersistencePort {
    Optional<ContactChannel> findById(String codContato);

    boolean existsById(String codContato);

    Page<ContactChannel> findAllByCanal(String codCanal, Pageable pageable);

    ContactChannel save(ContactChannel contactChannel);

    void deleteById(String codContato);
}
