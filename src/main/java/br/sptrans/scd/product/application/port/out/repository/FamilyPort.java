package br.sptrans.scd.product.application.port.out.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Family;

public interface FamilyPort {
    Optional<Family> findById(String codFamilia);
    boolean existsById(String codFamilia);
    Page<Family> findAll(String codStatus, Pageable pageable);
    Family save(Family family);
    void updateStatus(String codFamilia, String codStatus, Long idUsuario);
    void deleteById(String codFamilia);

    /**
     * Encontra o máximo código família numérico para auto-incremento.
     * Retorna 0 se nenhum código numérico existir.
     */
    Long findMaxNumericCode();
}
