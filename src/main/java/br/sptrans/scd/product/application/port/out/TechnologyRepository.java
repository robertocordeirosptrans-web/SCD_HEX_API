package br.sptrans.scd.product.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Technology;

public interface TechnologyRepository {

    Optional<Technology> findById(String codTecnologia);

    List<Technology> findAll();

    Technology save(Technology tecnologia);
}
