package br.sptrans.scd.product.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Species;

public interface SpeciesRepository {

    Optional<Species> findById(String codEspecie);

    boolean existsById(String codEspecie);

    List<Species> findAll(String codStatus);

    Species save(Species especies);

    void updateStatus(String codEspecie, String codStatus, Long idUsuario);

    void deleteById(String codEspecie);
}
