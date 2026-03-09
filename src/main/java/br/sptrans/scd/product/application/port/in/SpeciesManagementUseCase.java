package br.sptrans.scd.product.application.port.in;

import java.util.List;

import br.sptrans.scd.product.domain.Species;

/**
 * Porta de entrada para gestão de Espécies de produto.
 */
public interface SpeciesManagementUseCase {

    Species createSpecies(CreateSpeciesCommand command);

    Species updateSpecies(String codEspecie, UpdateSpeciesCommand command);

    Species findBySpecies(String codEspecie);

    List<Species> findAllSpecies(String codStatus);

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
