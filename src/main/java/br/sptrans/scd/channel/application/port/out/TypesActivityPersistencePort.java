package br.sptrans.scd.channel.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.channel.domain.TypesActivity;

public interface TypesActivityPersistencePort {
    Optional<TypesActivity> findById(String codAtividade);

    boolean existsById(String codAtividade);

    List<TypesActivity> findAll(String codStatus);

    TypesActivity save(TypesActivity typesActivity);

    void updateStatus(String codAtividade, String codStatus);

    void deleteById(String codAtividade);
}
