package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Species;

public interface SpeciesRepository {

    Optional<Species> findById(String codEspecie);

    boolean existsById(String codEspecie);

    List<Species> findAll(String codStatus);

    Page<Species> findAll(String codStatus, Pageable pageable);

    Species save(Species especies);

    void updateStatus(String codEspecie, String codStatus, Long idUsuario);

    void deleteById(String codEspecie);
}
