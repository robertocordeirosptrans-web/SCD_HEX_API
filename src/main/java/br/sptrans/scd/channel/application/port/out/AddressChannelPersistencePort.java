package br.sptrans.scd.channel.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.domain.AddressChannel;

public interface AddressChannelPersistencePort {
    Optional<AddressChannel> findById(String codEndereco);

    boolean existsById(String codEndereco);

    Page<AddressChannel> findAllByCanal(String codCanal, Pageable pageable);

    AddressChannel save(AddressChannel addressChannel);

    void deleteById(String codEndereco);

}
