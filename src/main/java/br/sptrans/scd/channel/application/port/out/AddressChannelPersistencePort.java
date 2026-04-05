package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.AddressChannel;

public interface AddressChannelPersistencePort {
    Optional<AddressChannel> findById(String codEndereco);

    boolean existsById(String codEndereco);

    List<AddressChannel> findAllByCanal(String codCanal);

    AddressChannel save(AddressChannel addressChannel);

    void deleteById(String codEndereco);

}
