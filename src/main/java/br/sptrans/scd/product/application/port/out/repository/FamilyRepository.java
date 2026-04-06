package br.sptrans.scd.product.application.port.out.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Family;

/**
 * Output Port para operações de persistência de Family.
 */
public interface FamilyRepository {

    Optional<Family> findById(String codFamilia);

    boolean existsById(String codFamilia);

    List<Family> findAll(String codStatus);

    Page<Family> findAll(String codStatus, Pageable pageable);

    Family save(Family family);

    void updateStatus(String codFamilia, String codStatus, Long idUsuario);

    void deleteById(String codFamilia);
}
