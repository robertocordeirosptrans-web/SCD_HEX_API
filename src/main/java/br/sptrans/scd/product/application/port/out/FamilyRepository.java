package br.sptrans.scd.product.application.port.out;

import java.util.List;
import java.util.Optional;

import br.sptrans.scd.product.domain.Family;

/**
 * Output Port para operações de persistência de Family.
 */
public interface FamilyRepository {

    Optional<Family> findById(String codFamilia);

    List<Family> findAll();

    Family save(Family family);
}
