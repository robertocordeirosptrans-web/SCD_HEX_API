package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.ContactChannel;

public interface ContactChannelRepository {

    Optional<ContactChannel> findById(String codContato);

    boolean existsById(String codContato);

    List<ContactChannel> findAllByCanal(String codCanal);

    ContactChannel save(ContactChannel contactChannel);

    void deleteById(String codContato);
}
