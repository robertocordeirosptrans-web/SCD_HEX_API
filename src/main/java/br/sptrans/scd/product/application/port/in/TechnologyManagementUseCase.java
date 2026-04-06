package br.sptrans.scd.product.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.sptrans.scd.product.domain.Technology;

/**
 * Porta de entrada para gestão de Tecnologias de produto.
 */
public interface TechnologyManagementUseCase {

    Technology createTechnology(CreateTechnologyCommand command);

    Technology updateTechnology(String codTecnologia, UpdateTechnologyCommand command);

    Technology findByTechnology(String codTecnologia);

    Page<Technology> findAllTechnologies(String codStatus, Pageable pageable);

    void activateTechnology(String codTecnologia, Long idUsuario);

    void inactivateTechnology(String codTecnologia, Long idUsuario);

    void deleteTechnology(String codTecnologia);

    // ── Commands ──────────────────────────────────────────────────────────────

    record CreateTechnologyCommand(
            String codTecnologia,
            String desTecnologia,
            Long idUsuario) {}

    record UpdateTechnologyCommand(
            String desTecnologia,
            Long idUsuario) {}
}
