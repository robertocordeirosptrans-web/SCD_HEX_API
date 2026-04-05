package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Technology;

public interface TechnologyRepository {

    Optional<Technology> findById(String codTecnologia);

    boolean existsById(String codTecnologia);

    List<Technology> findAll(String codStatus);

    Technology save(Technology tecnologia);

    void updateStatus(String codTecnologia, String codStatus, Long idUsuario);

    void deleteById(String codTecnologia);
}
