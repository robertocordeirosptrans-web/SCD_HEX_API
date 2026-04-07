package br.sptrans.scd.product.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import br.sptrans.scd.product.domain.Technology;

public interface TechnologyManagementUseCase {
    Technology create(CreateTechnologyCommand command);
    Technology update(String codTecnologia, UpdateTechnologyCommand command);
    void activate(String codTecnologia, Long idUsuario);
    void inactivate(String codTecnologia, Long idUsuario);
    void delete(String codTecnologia);
    Optional <Technology> findById(String codTecnologia);
    Page<Technology> findAll(String codStatus, Pageable pageable);

    // ── Commands ──────────────────────────────────────────────────────────────
    record CreateTechnologyCommand(
            String codTecnologia,
            String desTecnologia,
            Long idUsuario) {}

    record UpdateTechnologyCommand(
            String desTecnologia,
            Long idUsuario) {}
}
