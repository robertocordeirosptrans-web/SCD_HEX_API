package br.sptrans.scd.product.application.port.in;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Species;

public interface SpeciesManagementUseCase {
    Species create(CreateSpeciesCommand command);
    Species update(String codEspecie, UpdateSpeciesCommand command);
    void activate(String codEspecie, Long idUsuario);
    void inactivate(String codEspecie, Long idUsuario);
    void delete(String codEspecie);
    Optional <Species> findById(String codEspecie);
    Page<Species> findAll(String codStatus, Pageable pageable);

    // ── Commands ──────────────────────────────────────────────────────────────
    record CreateSpeciesCommand(
            String codEspecie,
            String desEspecie,
            Long idUsuario) {}

    record UpdateSpeciesCommand(
            String desEspecie,
            Long idUsuario) {}
}
