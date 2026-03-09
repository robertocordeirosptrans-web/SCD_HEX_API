package br.sptrans.scd.product.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Species;

public interface SpeciesRepository {

    Optional<Species> findById(String codEspecie);

    List<Species> findAll();

    Species save(Species especies);
}
