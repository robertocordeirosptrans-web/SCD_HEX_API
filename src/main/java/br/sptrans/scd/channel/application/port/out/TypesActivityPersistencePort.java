package br.sptrans.scd.channel.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.channel.domain.TypesActivity;

public interface TypesActivityPersistencePort {
    Optional<TypesActivity> findById(String codAtividade);

    boolean existsById(String codAtividade);

    Page<TypesActivity> findAll(String codStatus, Pageable pageable);

    TypesActivity save(TypesActivity typesActivity);

    void updateStatus(String codAtividade, String codStatus);

    void deleteById(String codAtividade);
}
