package br.sptrans.scd.product.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Species;

/**
 * Porta de entrada para gestão de Espécies de produto.
 */
public interface SpeciesManagementUseCase {

    Species createSpecies(CreateSpeciesCommand command);

    Species updateSpecies(String codEspecie, UpdateSpeciesCommand command);

    Species findBySpecies(String codEspecie);

    Page<Species> findAllSpecies(String codStatus, Pageable pageable);

    void activateSpecies(String codEspecie, Long idUsuario);

    void inactivateSpecies(String codEspecie, Long idUsuario);

    void deleteSpecies(String codEspecie);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateSpeciesCommand(
            String codEspecie,
            String desEspecie,
            Long idUsuario) {}

    record UpdateSpeciesCommand(
            String desEspecie,
            Long idUsuario) {}
}
