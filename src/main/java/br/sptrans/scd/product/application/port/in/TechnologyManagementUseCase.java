package br.sptrans.scd.product.application.port.in;

import java.util.List;

import br.sptrans.scd.product.domain.Technology;

/**
 * Porta de entrada para gestão de Tecnologias de produto.
 */
public interface TechnologyManagementUseCase {

    Technology createTechnology(CreateTechnologyCommand command);

    Technology updateTechnology(String codTecnologia, UpdateTechnologyCommand command);

    Technology findByTechnology(String codTecnologia);

    List<Technology> findAllTechnologies(String codStatus);

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
